cd P2Testing/
for file in *.ir;
do 
    cd ../
    echo Compiling $file
    java TigerBackend $file
    cd P2Testing/
done