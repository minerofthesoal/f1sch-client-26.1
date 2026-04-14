# f1sch - Toggle Fly

attribute @s minecraft:flying_speed modifier remove reachfly:fly_speed

execute if score @s f1sch.fly_on matches 0 run function f1sch:features/fly_on_enable
execute if score @s f1sch.fly_on matches 1 run function f1sch:features/fly_off

scoreboard players set @s f1sch.fly 0
