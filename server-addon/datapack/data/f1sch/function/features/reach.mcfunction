# f1sch - Toggle Reach

attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach

execute if score @s f1sch.reach_on matches 0 run function f1sch:features/reach_on
execute if score @s f1sch.reach_on matches 1 run function f1sch:features/reach_off

scoreboard players set @s f1sch.reach 0
