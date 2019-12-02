.data

.text

main:
sub $sp, $sp, 96
sw $ra, 16($sp)
l.s $f10, 32($sp)
li.s $f10, 1.0
s.s $f10, 32($sp)
li.s $f30, 1.0
li.s $f21, 1.0
li.s $f24, 1.0
li.s $f25, 1.0
li.s $f23, 1.0
l.s $f10, 56($sp)
li.s $f10, 1.0
s.s $f10, 56($sp)
li.s $f27, 1.0
l.s $f10, 64($sp)
li.s $f10, 1.0
s.s $f10, 64($sp)
li.s $f31, 1.0
li.s $f22, 1.0
li.s $f29, 1.0
li.s $f20, 1.0
l.s $f10, 84($sp)
li.s $f10, 1.0
s.s $f10, 84($sp)
l.s $f10, 88($sp)
li.s $f10, 1.0
s.s $f10, 88($sp)
li.s $f28, 1.0
l.s $f10, 96($sp)
li.s $f10, 1.0
s.s $f10, 96($sp)
li.s $f26, 0.0
li $s0, 0
li $s1, 0
loop_label0:
bgt $s0, 30,  loop_label1
l.s $f10, 32($sp)
l.s $f8, 32($sp)
li.s $f5, 1.0
add.s $f8, $f10, $f5
s.s $f10, 32($sp)
s.s $f8, 32($sp)
li.s $f5, 1.0
add.s $f30, $f30, $f5
li.s $f5, 1.0
add.s $f21, $f21, $f5
li.s $f5, 1.0
add.s $f24, $f24, $f5
li.s $f5, 1.0
add.s $f25, $f25, $f5
li.s $f5, 1.0
add.s $f23, $f23, $f5
l.s $f10, 56($sp)
l.s $f8, 56($sp)
li.s $f5, 1.0
add.s $f8, $f10, $f5
s.s $f10, 56($sp)
s.s $f8, 56($sp)
li.s $f5, 1.0
add.s $f27, $f27, $f5
l.s $f10, 64($sp)
l.s $f8, 64($sp)
li.s $f5, 1.0
add.s $f8, $f10, $f5
s.s $f10, 64($sp)
s.s $f8, 64($sp)
li.s $f5, 1.0
add.s $f31, $f31, $f5
li.s $f5, 1.0
add.s $f22, $f22, $f5
li.s $f5, 1.0
add.s $f29, $f29, $f5
li.s $f5, 1.0
add.s $f20, $f20, $f5
l.s $f10, 84($sp)
l.s $f8, 84($sp)
li.s $f5, 1.0
add.s $f8, $f10, $f5
s.s $f10, 84($sp)
s.s $f8, 84($sp)
l.s $f10, 88($sp)
l.s $f8, 88($sp)
li.s $f5, 1.0
add.s $f8, $f10, $f5
s.s $f10, 88($sp)
s.s $f8, 88($sp)
li.s $f5, 1.0
add.s $f28, $f28, $f5
l.s $f10, 96($sp)
l.s $f8, 96($sp)
li.s $f5, 1.0
add.s $f8, $f10, $f5
s.s $f10, 96($sp)
s.s $f8, 96($sp)
li $t1, 1
add $s0, $s0, $t1
j  loop_label0
loop_label1:
l.s $f9, 32($sp)
add.s $f26, $f26, $f9
s.s $f9, 32($sp)
add.s $f26, $f26, $f30
add.s $f26, $f26, $f21
add.s $f26, $f26, $f24
add.s $f26, $f26, $f25
add.s $f26, $f26, $f23
l.s $f9, 56($sp)
add.s $f26, $f26, $f9
s.s $f9, 56($sp)
add.s $f26, $f26, $f27
l.s $f9, 64($sp)
add.s $f26, $f26, $f9
s.s $f9, 64($sp)
add.s $f26, $f26, $f31
add.s $f26, $f26, $f22
add.s $f26, $f26, $f29
l.s $f9, 84($sp)
add.s $f26, $f26, $f9
s.s $f9, 84($sp)
add.s $f26, $f26, $f20
l.s $f9, 88($sp)
add.s $f26, $f26, $f9
s.s $f9, 88($sp)
add.s $f26, $f26, $f28
l.s $f9, 96($sp)
add.s $f26, $f26, $f9
s.s $f9, 96($sp)
loop_label2:
li.s $f5, 0.0
c.lt.s $f26, $f5
bc1t  loop_label3
li.s $f5, 1.0
sub.s $f26, $f26, $f5
li $t1, 1
add $s1, $s1, $t1
j  loop_label2
loop_label3:
move $a0, $s1
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 96
jr $ra
