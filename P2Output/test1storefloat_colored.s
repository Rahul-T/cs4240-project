.data
Y: .space 400
index: .space 4
X: .space 400
value: .space 4

.text

test:
sub $sp, $sp, 16
sw $ra, 16($sp)
lw $s0, index
l.s $f20, value
la $t0,  Y
mulo $t1, $s0, 4
add $t0, $t0, $t1
mov.s $f20, $f20
s.s $f20, ($t0)
lw $ra, 16($sp)
addi $sp, $sp, 16
jr $ra

main:
sub $sp, $sp, 68
sw $ra, 16($sp)
li $s0, 99
sw $s0, index
li.s $f20, 9.0
s.s $f20, value
li $s0, 98
li.s $f20, 9.0
li $t0, 0
Y_init_start:
la $t1,  Y
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li.s $f4,  10.0
s.s $f4, ($t1)
addi $t0, 1
blt $t0,  100,  Y_init_start
li $t0, 0
X_init_start:
la $t1,  X
li $t2, 4
mul $t3, $t0, $t2
add $t1, $t1, $t3
li.s $f4,  10.0
s.s $f4, ($t1)
addi $t0, 1
blt $t0,  100,  X_init_start
li.s $f23, 0.0
li $s1, 0
sw $s0, 60($sp)
s.s $f20, 64($sp)
s.s $f23, 68($sp)
jal  test
lw $s0, 60($sp)
l.s $f20, 64($sp)
l.s $f23, 68($sp)
la $t0,  X
li $t2, 99
mulo $t1, $t2, 4
add $t0, $t0, $t1
li.s $f5, 9.0
s.s $f5, ($t0)
la $t0,  X
mulo $t1, $s0, 4
add $t0, $t0, $t1
mov.s $f20, $f20
s.s $f20, ($t0)
li $s0, 0
loop_label0:
bgt $s0, 99,  loop_label1
la $t0,  X
mulo $t1, $s0, 4
add $t0, $t0, $t1
l.s $f21, ($t0)
la $t0,  Y
mulo $t1, $s0, 4
add $t0, $t0, $t1
l.s $f22, ($t0)
mul.s $f20, $f21, $f22
add.s $f21, $f23, $f20
mov.s $f23, $f21
li $t1, 1
add $s0, $s0, $t1
j  loop_label0
loop_label1:
mov.s $f12, $f23
li $v0, 2
syscall
lw $ra, 16($sp)
addi $sp, $sp, 68
jr $ra
