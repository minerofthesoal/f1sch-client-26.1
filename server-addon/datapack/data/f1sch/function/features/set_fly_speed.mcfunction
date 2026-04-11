# f1sch - Set Fly Speed

title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Fly speed set to ","color":"yellow"},{"score":{"name":"@s","objective":"f1sch.fly_speed"},"color":"green"}]

execute if score @s f1sch.fly_on matches 1 run function f1sch:features/fly

scoreboard players set @s f1sch.fly_speed 0
