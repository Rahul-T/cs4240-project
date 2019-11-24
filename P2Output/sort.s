.data
A: .space 40

.text

quicksort:
sub $sp, $sp, 68
sw $ra, 16($sp)
sw $a0, 52($sp)
sw $a1, 56($sp)
li $s0,  0
sw $s0, 28($sp)
li $s0,  0
sw $s0, 32($sp)
lw $s0, 52($sp)
lw $s1, 56($sp)
bge $s0, $s1,  end_sort
lw $s0, 52($sp)
lw $s1, 56($sp)
add $s2, $s0, $s1
sw $s2, 20($sp)
lw $s0, 20($sp)
li $s1,  2
div $s2, $s0, $s1
sw $s2, 20($sp)
la $t0,  A
lw $s0, 20($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, 24($sp)
lw $s0, 52($sp)
li $s1,  1
sub $s2, $s0, $s1
sw $s2, 28($sp)
lw $s0, 56($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 32($sp)
loop0_sort:
loop1_sort:
lw $s0, 28($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 28($sp)
la $t0,  A
lw $s0, 28($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, 44($sp)
lw $s0, 44($sp)
sw $s0, 36($sp)
lw $s0, 36($sp)
lw $s1, 24($sp)
blt $s0, $s1,  loop1_sort
loop2_sort:
lw $s0, 32($sp)
li $s1,  1
sub $s2, $s0, $s1
sw $s2, 32($sp)
la $t0,  A
lw $s0, 32($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, 44($sp)
lw $s0, 44($sp)
sw $s0, 40($sp)
lw $s0, 40($sp)
lw $s1, 24($sp)
bgt $s0, $s1,  loop2_sort
lw $s0, 28($sp)
lw $s1, 32($sp)
bge $s0, $s1,  exit0_sort
la $t0,  A
lw $s0, 32($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, 36($sp)
sw $s1, ($t0)
la $t0,  A
lw $s0, 28($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, 40($sp)
sw $s1, ($t0)
j  loop0_sort
exit0_sort:
lw $s0, 32($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 48($sp)
lw $a0, 52($sp)
lw $a1, 32($sp)
sw $s0, 60($sp)
sw $s1, 64($sp)
sw $s2, 68($sp)
jal  quicksort
lw $s0, 60($sp)
lw $s1, 64($sp)
lw $s2, 68($sp)
lw $s0, 32($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 32($sp)
lw $a0, 32($sp)
lw $a1, 56($sp)
sw $s0, 60($sp)
sw $s1, 64($sp)
sw $s2, 68($sp)
jal  quicksort
lw $s0, 60($sp)
lw $s1, 64($sp)
lw $s2, 68($sp)
end_sort:
li $v0,  1
lw $ra, 16($sp)
addi $sp, $sp, 68
jr $ra

main:
sub $sp, $sp, 36
sw $ra, 16($sp)
li $s0,  0
sw $s0, 28($sp)
li $s0,  10
sw $s0, 20($sp)
la $t0,  A
li $s0,  0
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  10
sw $s1, ($t0)
la $t0,  A
li $s0,  1
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  9
sw $s1, ($t0)
la $t0,  A
li $s0,  2
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  8
sw $s1, ($t0)
la $t0,  A
li $s0,  3
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  7
sw $s1, ($t0)
la $t0,  A
li $s0,  4
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  6
sw $s1, ($t0)
la $t0,  A
li $s0,  5
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  5
sw $s1, ($t0)
la $t0,  A
li $s0,  6
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  4
sw $s1, ($t0)
la $t0,  A
li $s0,  7
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  3
sw $s1, ($t0)
la $t0,  A
li $s0,  8
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  2
sw $s1, ($t0)
la $t0,  A
li $s0,  9
mulo $s0, $s0, 4
add $t0, $t0, $s0
li $s1,  1
sw $s1, ($t0)
li $a0,  0
lw $a1, 20($sp)
sw $s0, 32($sp)
sw $s1, 36($sp)
jal  quicksort
sw $v0, 28($sp)
lw $s0, 32($sp)
lw $s1, 36($sp)
li $s0,  0
sw $s0, 24($sp)
loop1:
lw $s0, 24($sp)
lw $s1, 20($sp)
bgt $s0, $s1,  exit1
la $t0,  A
lw $s0, 24($sp)
mulo $s0, $s0, 4
add $t0, $t0, $s0
lw $s1, ($t0)
sw $s1, 28($sp)
lw $a0, 28($sp)
li $v0, 1
syscall
lw $s0, 24($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 24($sp)
j  loop1
exit1:
return:
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra
