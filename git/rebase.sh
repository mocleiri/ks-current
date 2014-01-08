cd ks-api
echo "rebase ks-api changes"
git svn rebase
cd ../ks-core
echo "rebase ks-core changes"
git svn rebase
cd ../ks-deployments
echo "rebase ks-deployments changes"
git svn rebase
cd ../ks-parent
echo "rebase ks-parent changes"
git svn rebase
cd ../ks-enroll
echo "rebase ks-enroll changes"
git svn rebase
cd ../ks-lum
echo "rebase ks-lum changes"
git svn rebase

