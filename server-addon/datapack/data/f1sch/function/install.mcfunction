# f1sch Server Addon v3 - Datapack Edition
# Run on load / /reload to set up scoreboards

gamerule sendCommandFeedback false
gamerule logAdminCommands false

scoreboard objectives add f1sch.knockback trigger "f1sch Knockback Toggle"
scoreboard objectives add f1sch.reach trigger "f1sch Reach Toggle"
scoreboard objectives add f1sch.speed trigger "f1sch Speed Toggle"
scoreboard objectives add f1sch.nofall trigger "f1sch NoFall Toggle"
scoreboard objectives add f1sch.fly trigger "f1sch Fly Toggle"
scoreboard objectives add f1sch.tp trigger "f1sch Teleport Trigger"
scoreboard objectives add f1sch.kb_str trigger "f1sch Knockback Strength"
scoreboard objectives add f1sch.reach_dist trigger "f1sch Reach Distance"
scoreboard objectives add f1sch.speed_mult trigger "f1sch Speed Multiplier"
scoreboard objectives add f1sch.fly_speed trigger "f1sch Fly Speed"
scoreboard objectives add f1sch.tp_x trigger "f1sch TP X"
scoreboard objectives add f1sch.tp_y trigger "f1sch TP Y"
scoreboard objectives add f1sch.tp_z trigger "f1sch TP Z"
scoreboard objectives add f1sch.kb_on dummy
scoreboard objectives add f1sch.reach_on dummy
scoreboard objectives add f1sch.speed_on dummy
scoreboard objectives add f1sch.nofall_on dummy
scoreboard objectives add f1sch.fly_on dummy
scoreboard objectives add f1sch.op trigger "f1sch OP Self"
scoreboard objectives add f1sch.give trigger "f1sch Item Give"
scoreboard objectives add f1sch.help trigger "f1sch Help"

tellraw @a [{"text":"[f1sch] ","color":"gold","bold":true},{"text":"Server Addon v3 installed for MC 26.1","color":"green"}]
