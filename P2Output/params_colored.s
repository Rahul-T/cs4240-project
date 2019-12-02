.data

.text

test:
sub $sp, $sp, 56
sw $ra, 16($sp)
sw $a0, 20($sp)
s.s $f13, 40($sp)
sw $a2, 24($sp)
s.s $f15, 44($sp)
lw $s1, 20($sp)
lw $s2, 24($sp)
add $s0, $s1, $s2
move $a0, $s0
li $v0, 1
syscall
l.s $f20, 40($sp)
l.s $f21, 44($sp)
add.s $f22, $f20, $f21
mov.s $f12, $f22
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 56
jr $ra

test2:
sub $sp, $sp, 36
sw $ra, 16($sp)
sw $a0, 20($sp)
sw $a1, 24($sp)
sw $a2, 28($sp)
sw $a3, 32($sp)
lw $s0, 20($sp)
lw $s3, 24($sp)
add $s2, $s0, $s3
lw $s1, 28($sp)
add $s2, $s2, $s1
lw $s4, 32($sp)
add $s2, $s2, $s4
move $a0, $s2
li $v0, 1
syscall
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra

test3:
sub $sp, $sp, 36
sw $ra, 16($sp)
s.s $f12, 20($sp)
s.s $f13, 24($sp)
s.s $f14, 28($sp)
s.s $f15, 32($sp)
l.s $f21, 20($sp)
l.s $f20, 24($sp)
add.s $f24, $f21, $f20
l.s $f22, 28($sp)
add.s $f24, $f24, $f22
l.s $f23, 32($sp)
add.s $f24, $f24, $f23
mov.s $f12, $f24
li $v0, 2
syscall
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra

main:
sub $sp, $sp, 36
sw $ra, 16($sp)
li $a0, 1
li.s $f13, 1.0
li $a2, 2
li.s $f15, 2.0
jal  test
li $a0, 1
li $a1, 2
li $a2, 3
li $a3, 4
jal  test2
li.s $f12, 1.0
li.s $f13, 2.0
li.s $f14, 3.0
li.s $f15, 4.0
jal  test3
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra
