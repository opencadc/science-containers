#this program consists of all of the code in the galario quickstart guide
# they note everything until the plotting with nthreads=4 takes 5-8 min
# on a standard laptop

#HK added
import numpy as np
from galario import double as g_double

u, v, Re, Im, w = np.require(np.loadtxt("uvtable.txt", unpack=True), requirements='C')
wle = 1e-3  # [m]
u /= wle
v /= wle

from galario.double import get_image_size
nxy, dxy = get_image_size(u, v, verbose=True)

from galario import arcsec
def GaussianProfile(f0, sigma, Rmin, dR, nR):
    """ Gaussian brightness profile. """
    # radial grid
    R = np.linspace(Rmin, Rmin + dR*nR, nR, endpoint=False)
    return f0 * np.exp(-0.5*(R/sigma)**2)

from emcee import EnsembleSampler

# radial grid parameters
Rmin = 1e-4  # arcsec
dR = 0.005 #HK modified based on error messages 0.01    # arcsec
nR = 2000

# parameter space domain
p_ranges = [[1, 20],
            [0., 8.],
            [0., 90.],
            [0., 180.],
            [-2., 2.],
            [-2., 2.]]

ndim = len(p_ranges)        # number of dimensions
nwalkers = 40               # number of walkers
nthreads = 1                # CPU threads that emcee should use; HK changed to 1
#HK moves a bit later, after lnpostfn definition
#sampler = EnsembleSampler(nwalkers, ndim, lnpostfn,
#                          args=[p_ranges, Rmin, dR, nR, nxy, dxy, u, v, Re, Im, w],
#                          threads=nthreads)

from galario import deg, arcsec
from galario.double import chi2Profile

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

#HK moved below 3 lines here
sampler = EnsembleSampler(nwalkers, ndim, lnpostfn,
                          args=[p_ranges, Rmin, dR, nR, nxy, dxy, u, v, Re, Im, w],
                          threads=nthreads)

def lnpriorfn(p, par_ranges):
    """ Uniform prior probability function """

    for i in range(len(p)):
        if p[i] < par_ranges[i][0] or p[i] > par_ranges[i][1]:
            return -np.inf

    jacob = -p[0]       # jacobian of the log transformation

    return jacob

nsteps = 3000     # total number of MCMC steps

# initial guess for the parameters
p0 = [10, 0.5, 70., 60., 0., 0.] #  3 parameters for the model + 4 (inc, PA, dRA, dDec)

# initialize the walkers with an ndim-dimensional Gaussian ball
pos = [p0 + 1e-4*np.random.randn(ndim) for i in range(nwalkers)]

# execute the MCMC
pos, prob, state = sampler.run_mcmc(pos, nsteps)#HK commented out, rstate0=state, lnprob0=prob)

# do the corner plot
import corner
samples = sampler.chain[:, -1000:, :].reshape((-1, ndim))
fig = corner.corner(samples, labels=["$f_0$", "$\sigma$", r"$i$", r"PA", r"$\Delta$RA", r"$\Delta$Dec"],
                    show_titles=True, quantiles=[0.16, 0.50, 0.84],
                    label_kwargs={'labelpad':20, 'fontsize':0}, fontsize=8)
fig.savefig("triangle_example.png")

# do the uv-plot
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

from uvplot import UVTable

uvbin_size = 30e3     # uv-distance bin, units: wle

# observations uv-plot
uv = UVTable(uvtable=[u*wle, v*wle, Re, Im, w], wle=wle)
uv.apply_phase(-dRA, -dDec)         # center the source on the phase center
uv.deproject(inc, PA)
axes = uv.plot(linestyle='.', color='k', label='Data', uvbin_size=uvbin_size)

# model uv-plot
uv_mod = UVTable(uvtable=[u*wle, v*wle, vis_mod.real, vis_mod.imag, w], wle=wle)
uv_mod.apply_phase(-dRA, -dDec)     # center the source on the phase center
uv_mod.deproject(inc, PA)
uv_mod.plot(axes=axes, linestyle='-', color='r', label='Model', yerr=False, uvbin_size=uvbin_size)

axes[0].figure.savefig("uvplot_example.pdf")

