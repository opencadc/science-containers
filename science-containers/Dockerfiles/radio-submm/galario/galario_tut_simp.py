import os
os.environ["OMP_NUM_THREADS"] = "1"
from schwimmbad import MPIPool
from galario import deg, arcsec
import matplotlib.pyplot as plt
import matplotlib.colors as colors
import time
from galario import double as g_double
from galario import single as g_single
from galario.double import chi2Profile
import numpy as np
from emcee import EnsembleSampler
from galario.double import get_image_size
from galario.double import sweep
from galario.double import chi2Image
from galario.double import sampleImage
import corner
from uvplot import UVTable
from uvplot import COLUMNS_V0
import sys

########################################################


# Load in UV table data fuke
u, v, Re, Im, w = np.require(np.loadtxt("uvtable.txt", unpack=True), requirements='C')
wle = 1e-3  # [m]
u /= wle
v /= wle



nxy, dxy = get_image_size(u, v, verbose=True)

def GaussianProfile(f0, sigma, Rmin, dR, nR):
    """ Gaussian brightness profile. """

    # radial grid
    R = np.linspace(Rmin, Rmin + dR*nR, nR, endpoint=False)

    return f0 * np.exp(-0.5*(R/sigma)**2)



def lnpostfn(p, p_ranges, Rmin, dR, nR, nxy, dxy, u, v, Re, Im, w):
    """ Log of posterior probability function """

    lnprior = lnpriorfn(p, p_ranges)  # apply prior
    if not np.isfinite(lnprior):
        return -np.inf

    # unpack the parameters
    f0, sigma, inc, PA, dRA, dDec = p

    f0 = 10.**f0        # convert from log to real space

    # convert to radians
    sigma *= arcsec
    Rmin *= arcsec
    dR *= arcsec
    inc *= deg
    PA *= deg
    dRA *= arcsec
    dDec *= arcsec

    # compute the model brightness profile
    f = GaussianProfile(f0, sigma, Rmin, dR, nR)

    chi2 = chi2Profile(f, Rmin, dR, nxy, dxy, u, v, Re, Im, w,
                       inc=inc, PA=PA, dRA=dRA, dDec=dDec)

    return -0.5 * chi2 + lnprior

def lnpriorfn(p, par_ranges):
    """ Uniform prior probability function """

    for i in range(len(p)):
        if p[i] < par_ranges[i][0] or p[i] > par_ranges[i][1]:
            return -np.inf

    jacob = -p[0]       # jacobian of the log transformation

    return jacob



# radial grid parameters
Rmin = 1e-4  # arcsec
dR = 0.001    # arcsec
nR = 2000

# parameter space domain
p_ranges = [[1, 20],
            [0., 8.],
            [0., 90.],
            [0., 180.],
            [-2., 2.],
            [-2., 2.]]

'''
################################################################

ndim = len(p_ranges)        # number of dimensions
nwalkers = 40               # number of walkers

nthreads = 4                # CPU threads that emcee should use

sampler = EnsembleSampler(nwalkers, ndim, lnpostfn,
                          args=[p_ranges, Rmin, dR, nR, nxy, dxy, u, v, Re, Im, w],
                          threads=nthreads)



nsteps = 300     # total number of MCMC steps

# initial guess for the parameters
p0 = [10, 0.5, 70., 60., 0., 0.] #  3 parameters for the model + 4 (inc, PA, dRA, dDec)

# initialize the walkers with an ndim-dimensional Gaussian ball
pos = [p0 + 1e-4*np.random.randn(ndim) for i in range(nwalkers)]

# execute the MCMC
pos, prob, state = sampler.run_mcmc(pos, nsteps, progress=True)
'''
####################################################################

with MPIPool() as pool:
    if not pool.is_master():
        pool.wait()
        sys.exit(0)
    
    ndim =len(p_ranges)
    nwalkers = 40
    nthreads=4
    sampler = EnsembleSampler(nwalkers, ndim, lnpostfn, args=[p_ranges, Rmin, dR, nR, nxy, dxy, u, v, Re, Im, w],
                               threads=nthreads)
    nsteps = 3000
    
    p0 = [10, 0.5, 70, 60, 0, 0]
    
    pos = [p0 + 1e-4*np.random.randn(ndim) for i in range(nwalkers)]
    
    pos, prob, state = sampler.run_mcmc(pos, nsteps, progress=True)

#####################################################################

# do the corner plot
samples = sampler.chain[:, -1000:, :].reshape((-1, ndim))
fig = corner.corner(samples, labels=["$f_0$", "$\sigma$", r"$i$", r"PA", r"$\Delta$RA", r"$\Delta$Dec"],
                    show_titles=True, quantiles=[0.16, 0.50, 0.84],
                    label_kwargs={'labelpad':20, 'fontsize':0}, fontsize=8)
fig.savefig("triangle_simp.png")

#do the uv-plot
# select the bestfit model (here, e.g., the model with median parameters)
bestfit = [np.percentile(samples[:, i], 50) for i in range(ndim)]

f0, sigma, inc, PA, dRA, dDec = bestfit

f0 = 10.**f0        # convert from log to real space

# convert to radians
sigma *= arcsec
Rmin *= arcsec
dR *= arcsec
inc *= deg
PA *= deg
dRA *= arcsec
dDec *= arcsec

f = GaussianProfile(f0, sigma, Rmin, dR, nR)

# compute the visibilities of the bestfit model
vis_mod = g_double.sampleProfile(f, Rmin, dR, nxy, dxy, u, v,
                                 inc=inc, PA=PA, dRA=dRA, dDec=dDec)



uvbin_size = 30e3     # uv-distance bin, units: wle

# observations uv-plot
uv = UVTable(uvtable=[u*wle, v*wle, Re, Im, w], wle=wle, columns=COLUMNS_V0)
uv.apply_phase(-dRA, -dDec)         # center the source on the phase center
uv.deproject(inc, PA)
axes = uv.plot(linestyle='.', color='k', label='Data', uvbin_size=uvbin_size)

# model uv-plot
uv_mod = UVTable(uvtable=[u*wle, v*wle, vis_mod.real, vis_mod.imag, w], wle=wle, columns=COLUMNS_V0)
uv_mod.apply_phase(-dRA, -dDec)     # center the source on the phase center
uv_mod.deproject(inc, PA)
uv_mod.plot(axes=axes, linestyle='-', color='r', label='Model', yerr=False, uvbin_size=uvbin_size)

axes[0].figure.savefig("uvplot_simp.pdf")