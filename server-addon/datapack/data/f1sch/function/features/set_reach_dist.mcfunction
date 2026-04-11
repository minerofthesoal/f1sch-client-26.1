# f1sch - Set Reach Distance

title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Reach distance set to ","color":"yellow"},{"score":{"name":"@s","objective":"f1sch.reach_dist"},"color":"green"}]

execute if score @s f1sch.reach_on matches 1 run function f1sch:features/reach

scoreboard players set @s f1sch.reach_dist 0
