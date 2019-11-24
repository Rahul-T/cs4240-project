.data

.text

main:
sub $sp, $sp, 52
sw $ra, 16($sp)
li $s0,  0
sw $s0, 20($sp)
li.s $f20,  25.0
s.s $f20, 24($sp)
li.s $f20,  0.000001
s.s $f20, 28($sp)
l.s $f20, 24($sp)
s.s $f20, 32($sp)
l.s $f20, 32($sp)
s.s $f20, 36($sp)
li.s $f20,  0.
s.s $f20, 40($sp)
li.s $f20,  0.
s.s $f20, 44($sp)
li.s $f20,  0.
s.s $f20, 52($sp)
l.s $f20, 24($sp)
li.s $f21,  0.
c.lt.s $f20, $f21
bc1f  EXIT
LABEL0:
l.s $f20, 32($sp)
l.s $f21, 32($sp)
mul.s $f22, $f20, $f21
s.s $f22, 40($sp)
l.s $f20, 40($sp)
l.s $f21, 24($sp)
sub.s $f22, $f20, $f21
s.s $f22, 48($sp)
l.s $f20, 48($sp)
s.s $f20, 44($sp)
l.s $f20, 44($sp)
li.s $f21,  0.
c.lt.s $f21, $f20
bc1f  LABEL1
li.s $f20,  0.
l.s $f21, 44($sp)
sub.s $f22, $f20, $f21
s.s $f22, 48($sp)
l.s $f20, 48($sp)
s.s $f20, 44($sp)
LABEL1:
l.s $f20, 44($sp)
l.s $f21, 28($sp)
c.le.s $f20, $f21
bc1f LABEL2
l.s $f20, 32($sp)
s.s $f20, 36($sp)
l.s $f20, 24($sp)
l.s $f21, 32($sp)
div.s $f22, $f20, $f21
s.s $f22, 52($sp)
l.s $f20, 52($sp)
l.s $f21, 32($sp)
add.s $f22, $f20, $f21
s.s $f22, 52($sp)
l.s $f20, 52($sp)
li.s $f21,  2.
div.s $f22, $f20, $f21
s.s $f22, 52($sp)
l.s $f20, 52($sp)
s.s $f20, 32($sp)
lw $s0, 20($sp)
li $s1,  1
add $s2, $s0, $s1
sw $s2, 20($sp)
l.s $f20, 36($sp)
l.s $f21, 32($sp)
c.eq.s $f20, $f21
bc1f  LABEL2
j  LABEL0
LABEL2:
lw $a0, 32($sp)
li $v0, 1
syscall
EXIT:
lw $ra, 16($sp)
addi $sp, $sp, 40
jr $ra