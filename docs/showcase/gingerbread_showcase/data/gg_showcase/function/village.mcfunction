place template gingerbread_grove:gingerbread_house ~4 ~-1 ~6
place template gingerbread_grove:santas_workshop ~-20 ~-1 ~6
fill ~-3 ~-1 ~16 ~-1 ~-1 ~18 gingerbread_grove:frosted_gingerbread
place feature gingerbread_grove:festive_fir_tree ~-2 ~ ~17
place feature gingerbread_grove:gift_boxes ~-2 ~ ~17
fill ~16 ~-1 ~8 ~18 ~-1 ~10 gingerbread_grove:frosted_gingerbread
place feature gingerbread_grove:giant_candy_cane ~17 ~ ~9
tellraw @s {"text":"Village built to the south: house, workshop (with Santa and elves), tree and a giant candy cane.","color":"green"}
