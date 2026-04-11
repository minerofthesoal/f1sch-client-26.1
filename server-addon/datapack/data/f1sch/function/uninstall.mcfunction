# f1sch Server Addon v3 - Uninstall

execute as @a run attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost
execute as @a run attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
execute as @a run attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach
execute as @a run attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost
execute as @a run attribute @s minecraft:fall_damage_multiplier modifier remove reachfly:nofall
execute as @a run attribute @s minecraft:safe_fall_distance modifier remove reachfly:nofall_safe

tag @a remove f1sch.kb_active

scoreboard objectives remove f1sch.knockback
scoreboard objectives remove f1sch.reach
scoreboard objectives remove f1sch.speed
scoreboard objectives remove f1sch.nofall
scoreboard objectives remove f1sch.fly
scoreboard objectives remove f1sch.tp
scoreboard objectives remove f1sch.kb_str
scoreboard objectives remove f1sch.reach_dist
scoreboard objectives remove f1sch.speed_mult
scoreboard objectives remove f1sch.fly_speed
scoreboard objectives remove f1sch.tp_x
scoreboard objectives remove f1sch.tp_y
scoreboard objectives remove f1sch.tp_z
scoreboard objectives remove f1sch.kb_on
scoreboard objectives remove f1sch.reach_on
scoreboard objectives remove f1sch.speed_on
scoreboard objectives remove f1sch.nofall_on
scoreboard objectives remove f1sch.fly_on
scoreboard objectives remove f1sch.help
scoreboard objectives remove f1sch.op
scoreboard objectives remove f1sch.give

gamerule sendCommandFeedback true
gamerule logAdminCommands true

tellraw @a [{"text":"[f1sch] ","color":"gold","bold":true},{"text":"Server Addon v3 uninstalled.","color":"red"}]
