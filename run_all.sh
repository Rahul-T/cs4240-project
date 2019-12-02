if [ -z "$SPIMCOMMAND" ]
then
      echo "Instead of inputing spim command everytime, set the SPIMCOMMAND environment variable!"
      echo "Input spim command:"
      read SPIMCOMMAND
fi

SPIM_FLAGS=" -keepstats -f "

for file in P2Output/*.s;
do
    echo "Running $file"
    $SPIMCOMMAND $SPIM_FLAGS $file
    echo
done