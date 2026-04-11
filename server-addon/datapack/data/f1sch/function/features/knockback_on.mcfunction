# f1sch - Enable Knockback

execute unless score @s f1sch.kb_str matches 1.. run scoreboard players set @s f1sch.kb_str 5

execute store result storage f1sch:temp value int 1 run scoreboard players get @s f1sch.kb_str
function f1sch:features/macros/apply_knockback with storage f1sch:temp

scoreboard players set @s f1sch.kb_on 1
tag @s add f1sch.kb_active

tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"enabled","color":"green"}]
