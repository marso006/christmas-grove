kill @e[tag=gg_showcase]
summon gingerbread_grove:gingerbread_man ~-12 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b}
summon gingerbread_grove:peppermint_pup ~-9 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b}
summon gingerbread_grove:elf ~-6 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b}
summon gingerbread_grove:santa_claus ~-3 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b}
summon gingerbread_grove:reindeer ~0 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b,Tame:1b,SaddleItem:{id:"minecraft:saddle",count:1}}
summon gingerbread_grove:cookie_soldier ~3 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b}
summon gingerbread_grove:gumdrop_blob ~6 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b,Size:1}
summon gingerbread_grove:evil_snowman ~9 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b}
summon gingerbread_grove:krampus ~12 ~ ~6 {NoAI:1b,Rotation:[180f,0f],Tags:["gg_showcase"],PersistenceRequired:1b}
tellraw @s {"text":"Mob lineup placed 6 blocks south of you. /function gg_showcase:cleanup removes it.","color":"green"}
