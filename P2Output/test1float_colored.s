.data
Y: .space 400
X: .space 400

.text

main:
sub $sp, $sp, 40
sw $ra, 16($sp)
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
li.s $f21, 0.0
li $s0, 0
li $s0, 0
loop_label0:
bgt $s0, 99,  loop_label1
la $t0,  X
mulo $t1, $s0, 4
add $t0, $t0, $t1
l.s $f22, ($t0)
la $t0,  Y
mulo $t1, $s0, 4
add $t0, $t0, $t1
l.s $f20, ($t0)
mul.s $f23, $f22, $f20
add.s $f20, $f21, $f23
mov.s $f21, $f20
li $t1, 1
add $s0, $s0, $t1
j  loop_label0
loop_label1:
mov.s $f12, $f21
li $v0, 2
syscall
lw $ra, 16($sp)
addi $sp, $sp, 40
jr $ra
