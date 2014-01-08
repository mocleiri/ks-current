cd ks-api
echo "fetch ks-api changes"
git svn fetch --all --log-window-size=1000
cd ../ks-core
echo "fetch ks-core changes"
git svn fetch --all --log-window-size=1000
cd ../ks-deployments
echo "fetch ks-deployments changes"
git svn fetch --all --log-window-size=1000
cd ../ks-parent
echo "fetch ks-parent changes"
git svn fetch --all --log-window-size=1000
cd ../ks-enroll
echo "fetch ks-enroll changes"
git svn fetch --all --log-window-size=1000
cd ../ks-lum
echo "fetch ks-lum changes"
git svn fetch --all --log-window-size=1000

