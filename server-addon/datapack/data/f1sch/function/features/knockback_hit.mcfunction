# f1sch - Knockback Hit (called by advancement)

advancement revoke @s only f1sch:knockback_hit

execute if entity @s[tag=f1sch.kb_active] run execute as @e[sort=nearest,limit=1,tag=!f1sch.kb_active,distance=..10] at @s run tp @s ~ ~0.8 ~
