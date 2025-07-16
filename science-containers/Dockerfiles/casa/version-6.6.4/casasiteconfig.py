#This is a CASA site configuration file for versions 6.6.4 or higher
#Used to point casa to location of CASA run-time data such as the leapsecond
# table, which for CANFAR is in a central site location
#This config file should ideally be located at /opt/casa/casasiteconfig.py

measurespath = "/arc/projects/casa-data-repository/"
#(note that current documentation suggests a final directory of either /data
# or /casarundata but neither of these directories are included in the
# data directory which we are rsync-ing from NRAO )
#turn off autoupdates b/c this is done by site staff not the user
measures_auto_update = False
data_auto_update = False
