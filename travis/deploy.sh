git config user.name "DunaMariusCosmin";
git config user.email "duna.marius.cosmin@gmail.com";
git fetch;
git checkout -f master;
git reset;
cp -f target/addon.xml build;
git add build/addon.xml;
git commit -m "New release - ${TRAVIS_TAG}";
git push origin HEAD:master; 