.data
global_int: .space 4
global_float: .space 4

.text

test:
sub $sp, $sp, 32
sw $ra, 16($sp)
sw $a0, 20($sp)
s.s $f13, 28($sp)
lw $s0, global_int
l.s $f20, global_float
l.s $f21, 28($sp)
add.s $f20, $f21, $f20
s.s $f20, global_float
lw $s1, 20($sp)
add $s0, $s1, $s0
sw $s0, global_int
lw $ra, 16($sp)
addi $sp, $sp, 32
jr $ra

main:
sub $sp, $sp, 52
sw $ra, 16($sp)
li $s2, 1
sw $s2, global_int
li.s $f20, 1.0
s.s $f20, global_float
li $s1, 1
li $s0, 0
li $a0, 5
li.s $f13, 6.0
sw $s0, 40($sp)
sw $s1, 44($sp)
sw $s2, 48($sp)
s.s $f20, 52($sp)
jal  test
lw $s0, 40($sp)
lw $s1, 44($sp)
lw $s2, global_int
l.s $f20, global_float
li.s $f5, 7.0
c.eq.s $f20, $f5
bc1f  label1
move $a0, $s1
li $v0, 1
syscall
j  label2
label1:
move $a0, $s0
li $v0, 1
syscall
label2:
bne $s2, 6,  label3
move $a0, $s1
li $v0, 1
syscall
j  ret
label3:
move $a0, $s0
li $v0, 1
syscall
ret:
lw $ra, 16($sp)
addi $sp, $sp, 52
jr $ra
