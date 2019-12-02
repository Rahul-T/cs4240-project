.data
A: .space 40

.text

quicksort:
sub $sp, $sp, 68
sw $ra, 16($sp)
sw $a0, 52($sp)
sw $a1, 56($sp)
li $s2, 0
li $s2, 0
lw $s0, 52($sp)
lw $s1, 56($sp)
bge $s0, $s1,  end_sort
add $s2, $s0, $s1
li $t1, 2
div $s2, $s2, $t1
la $t0,  A
mulo $t1, $s2, 4
add $t0, $t0, $t1
lw $s3, ($t0)
li $t1, 1
sub $s4, $s0, $t1
li $t1, 1
add $s2, $s1, $t1
loop0_sort:
loop1_sort:
li $t1, 1
add $s4, $s4, $t1
la $t0,  A
mulo $t1, $s4, 4
add $t0, $t0, $t1
lw $s6, ($t0)
move $s5, $s6
blt $s5, $s3,  loop1_sort
loop2_sort:
li $t1, 1
sub $s2, $s2, $t1
la $t0,  A
mulo $t1, $s2, 4
add $t0, $t0, $t1
lw $s7, ($t0)
move $s6, $s7
bgt $s6, $s3,  loop2_sort
bge $s4, $s2,  exit0_sort
la $t0,  A
mulo $t1, $s2, 4
add $t0, $t0, $t1
move $s5, $s5
sw $s5, ($t0)
la $t0,  A
mulo $t1, $s4, 4
add $t0, $t0, $t1
move $s6, $s6
sw $s6, ($t0)
j  loop0_sort
exit0_sort:
li $t1, 1
add $s3, $s2, $t1
move $a0, $s0
move $a1, $s2
sw $s1, 60($sp)
sw $s0, 64($sp)
sw $s2, 68($sp)
jal  quicksort
lw $s1, 60($sp)
lw $s0, 64($sp)
lw $s2, 68($sp)
li $t1, 1
add $s2, $s2, $t1
move $a0, $s2
move $a1, $s1
sw $s1, 60($sp)
sw $s2, 64($sp)
jal  quicksort
lw $s1, 60($sp)
lw $s2, 64($sp)
end_sort:
li $v0, 1
lw $ra, 16($sp)
addi $sp, $sp, 68
jr $ra

main:
sub $sp, $sp, 36
sw $ra, 16($sp)
li $s0, 0
li $s0, 10
la $t0,  A
li $t2, 0
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 10
sw $t3, ($t0)
la $t0,  A
li $t2, 1
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 9
sw $t3, ($t0)
la $t0,  A
li $t2, 2
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 8
sw $t3, ($t0)
la $t0,  A
li $t2, 3
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 7
sw $t3, ($t0)
la $t0,  A
li $t2, 4
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 6
sw $t3, ($t0)
la $t0,  A
li $t2, 5
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 5
sw $t3, ($t0)
la $t0,  A
li $t2, 6
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 4
sw $t3, ($t0)
la $t0,  A
li $t2, 7
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 3
sw $t3, ($t0)
la $t0,  A
li $t2, 8
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 2
sw $t3, ($t0)
la $t0,  A
li $t2, 9
mulo $t1, $t2, 4
add $t0, $t0, $t1
li $t3, 1
sw $t3, ($t0)
li $a0, 0
move $a1, $s0
sw $s1, 32($sp)
sw $s0, 36($sp)
jal  quicksort
sw $v0, 28($sp)
lw $s1, 32($sp)
lw $s0, 36($sp)
lw $s1, 28($sp)
li $s2, 0
loop1:
bgt $s2, $s0,  exit1
la $t0,  A
mulo $t1, $s2, 4
add $t0, $t0, $t1
lw $s1, ($t0)
move $a0, $s1
li $v0, 1
syscall
li $t1, 1
add $s2, $s2, $t1
j  loop1
exit1:
return:
lw $ra, 16($sp)
addi $sp, $sp, 36
jr $ra
