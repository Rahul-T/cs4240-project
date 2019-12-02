.data
global_int: .space 4
global_float: .space 4

.text

test:
sub $sp, $sp, 32
sw $ra, 16($sp)
sw $a0, 20($sp)
s.s $f13, 28($sp)
l.s $f20, 28($sp)
l.s $f21, global_float
add.s $f22, $f20, $f21
s.s $f22, global_float
lw $s0, 20($sp)
lw $s1, global_int
add $s2, $s0, $s1
sw $s2, global_int
lw $ra, 16($sp)
addi $sp, $sp, 32
jr $ra

main:
sub $sp, $sp, 44
sw $ra, 16($sp)
li $s0,  1
sw $s0, global_int
li.s $f20,  1.0
s.s $f20, global_float
li $s0,  1
sw $s0, 24($sp)
li $s0,  0
sw $s0, 28($sp)
li $a0, 5
li.s $f13, 6.0
sw $s0, 40($sp)
s.s $f20, 44($sp)
jal  test
lw $s0, 40($sp)
l.s $f20, 44($sp)
l.s $f20, global_float
li.s $f21,  7.0
c.eq.s $f20, $f21
bc1f  label1
lw $a0, 24($sp)
li $v0, 1
syscall
j  label2
label1:
lw $a0, 28($sp)
li $v0, 1
syscall
label2:
lw $s0, global_int
li $s1,  6
bne $s0, $s1,  label3
lw $a0, 24($sp)
li $v0, 1
syscall
j  ret
label3:
lw $a0, 28($sp)
li $v0, 1
syscall
ret:
lw $ra, 16($sp)
addi $sp, $sp, 44
jr $ra
