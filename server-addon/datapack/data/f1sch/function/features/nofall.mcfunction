# f1sch - Toggle NoFall

execute if score @s f1sch.nofall_on matches 0 run function f1sch:features/nofall_on_enable
execute if score @s f1sch.nofall_on matches 1 run function f1sch:features/nofall_off

scoreboard players set @s f1sch.nofall 0
