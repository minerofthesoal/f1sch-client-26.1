# f1sch - Toggle Speed

attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost

execute if score @s f1sch.speed_on matches 0 run function f1sch:features/speed_on
execute if score @s f1sch.speed_on matches 1 run function f1sch:features/speed_off

scoreboard players set @s f1sch.speed 0
