# f1sch - Set Knockback Strength

title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Knockback strength set to ","color":"yellow"},{"score":{"name":"@s","objective":"f1sch.kb_str"},"color":"green"}]

execute if score @s f1sch.kb_on matches 1 run function f1sch:features/knockback

scoreboard players set @s f1sch.kb_str 0
