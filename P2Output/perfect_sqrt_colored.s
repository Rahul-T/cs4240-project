.data

.text

main:
sub $sp, $sp, 56
sw $ra, 16($sp)
li $s0, 0
li.s $f20, 25.0
li.s $f21, 0.0
mov.s $f21, $f20
li.s $f22, 0.0
li.s $f22, 0.000001
mov.s $f23, $f21
li.s $f23, 0.0
li.s $f5, 0.0
c.lt.s $f20, $f5
bc1t  return_label
loop0_label:
mul.s $f23, $f21, $f21
sub.s $f24, $f23, $f20
mov.s $f23, $f24
li.s $f5, 0.0
c.lt.s $f23, $f5
bc1f  loop1_label
li.s $f4, 0.0
sub.s $f24, $f4, $f23
mov.s $f23, $f24
loop1_label:
c.le.s $f23, $f22
bc1t  exit_loop
mov.s $f23, $f21
div.s $f24, $f20, $f21
add.s $f24, $f24, $f21
li.s $f5, 2.0
div.s $f24, $f24, $f5
mov.s $f21, $f24
li $t1, 1
add $s0, $s0, $t1
c.eq.s $f23, $f21
bc1t  exit_loop
j  loop0_label
exit_loop:
mov.s $f12, $f21
li $v0, 1
syscall
return_label:
lw $ra, 16($sp)
addi $sp, $sp, 56
jr $ra
