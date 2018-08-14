#!/bin/sh

if [ ! -f advisor ]
then
	echo "Are you sure you're in the right directory?"
	exit 1
fi
if [ ! -d tests ]
then
	echo "No tests/ directory here. Have you unpacked it?"
	exit 1
fi

ulimit -t 10
for tt in tests/*.sh
do
	t=`basename $tt .sh`
	sh "$tt" "$BIN" 2>&1 | head -1000 > tests/$t.out
	if cmp -s tests/$t.exp tests/$t.out
	then
		echo Passed test $t
	else
		echo "$t"
		echo "$tt"
		echo Failed test $t
#		printf "%-35s%-35s\n\n" "Your Output" "Expected Output" > tests/$t.cmp
#		pr -m -t -w 70 tests/$t.out tests/$t.exp >> tests/$t.cmp
#		echo Check differences using \"cat tests/$t.cmp\"
		echo Check differences using \"diff tests/$t.exp tests/$t.out\"

	fi
done

