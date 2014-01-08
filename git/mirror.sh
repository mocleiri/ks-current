cd ks-api
echo "mirror ks-api changes"
git push mirror --mirror
cd ../ks-core
echo "mirror ks-core changes"
git push mirror --mirror
cd ../ks-deployments
echo "mirror ks-deployments changes"
git push mirror --mirror
cd ../ks-parent
echo "mirror ks-parent changes"
git push mirror --mirror
cd ../ks-enroll
echo "mirror ks-enroll changes"
git push mirror --mirror
cd ../ks-lum
echo "mirror ks-lum changes"
git push mirror --mirror

