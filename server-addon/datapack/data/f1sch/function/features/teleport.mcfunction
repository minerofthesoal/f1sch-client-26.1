# f1sch - Teleport to coordinates

execute store result storage f1sch:tp x int 1 run scoreboard players get @s f1sch.tp_x
execute store result storage f1sch:tp y int 1 run scoreboard players get @s f1sch.tp_y
execute store result storage f1sch:tp z int 1 run scoreboard players get @s f1sch.tp_z

function f1sch:features/macros/teleport with storage f1sch:tp

scoreboard players set @s f1sch.tp 0
scoreboard players set @s f1sch.tp_x 0
scoreboard players set @s f1sch.tp_y 0
scoreboard players set @s f1sch.tp_z 0
