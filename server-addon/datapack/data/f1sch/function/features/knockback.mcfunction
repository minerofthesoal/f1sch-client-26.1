# f1sch - Toggle Knockback

attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost

execute if score @s f1sch.kb_on matches 0 run function f1sch:features/knockback_on
execute if score @s f1sch.kb_on matches 1 run function f1sch:features/knockback_off

scoreboard players set @s f1sch.knockback 0
