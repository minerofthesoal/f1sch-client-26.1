# f1sch - Enable Fly

execute unless score @s f1sch.fly_speed matches 1.. run scoreboard players set @s f1sch.fly_speed 15

scoreboard players remove @s f1sch.fly_speed 10
execute store result storage f1sch:temp value float 0.005 run scoreboard players get @s f1sch.fly_speed
scoreboard players add @s f1sch.fly_speed 10

function f1sch:features/macros/apply_fly_speed with storage f1sch:temp

gamemode creative @s

scoreboard players set @s f1sch.fly_on 1

tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"Fly ","color":"light_purple"},{"text":"enabled","color":"green"}]
