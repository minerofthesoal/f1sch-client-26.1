# f1sch - Set Speed Multiplier

title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Speed multiplier set to ","color":"yellow"},{"score":{"name":"@s","objective":"f1sch.speed_mult"},"color":"green"}]

execute if score @s f1sch.speed_on matches 1 run function f1sch:features/speed

scoreboard players set @s f1sch.speed_mult 0
