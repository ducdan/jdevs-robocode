package jk.mega;

public class Insulter {


    public static String getInsult() {

        String word1 = column1[(int) (column1.length * Math.random())];
        String word2 = column2[(int) (column2.length * Math.random())];
        String word3 = column3[(int) (column3.length * Math.random())];
        return ("Thou " + word1 + " " + word2 + " " + word3 + "!");

    }


    static final String[] column1 = {"artless",
            "bawdy",
            "beslubbering",
            "bootless",
            "churlish",
            "cockered",
            "clouted",
            "craven",
            "currish",
            "dankish",
            "dissembling",
            "droning",
            "errant",
            "fawning",
            "fobbing",
            "froward",
            "frothy",
            "gleeking",
            "goatish",
            "gorbellied",
            "impertinent",
            "infectious",
            "jarring",
            "loggerheaded",
            "lumpish",
            "mammering",
            "mangled",
            "mewling",
            "paunchy",
            "pribbling",
            "puking",
            "puny",
            "qualling",
            "rank",
            "reeky",
            "roguish",
            "ruttish",
            "saucy",
            "spleeny",
            "spongy",
            "surly",
            "tottering",
            "unmuzzled",
            "vain",
            "venomed",
            "villainous",
            "warped",
            "wayward",
            "weedy",
            "yeasty"};
    static final String[] column2 = {" base-court",
            "bat-fowling",
            "beef-witted",
            "beetle-headed",
            "boil-brained",
            "clapper-clawed",
            "clay-brained",
            "common-kissing",
            "crook-pated",
            "dismal-dreaming",
            "dizzy-eyed",
            "doghearted",
            "dread-bolted",
            "earth-vexing",
            "elf-skinned",
            "fat-kidneyed",
            "fen-sucked",
            "flap-mouthed",
            "fly-bitten",
            "folly-fallen",
            "fool-born",
            "full-gorged",
            "guts-griping",
            "half-faced",
            "hasty-witted",
            "hedge-born",
            "hell-hated",
            "idle-headed",
            "ill-breeding",
            "ill-nurtured",
            "knotty-pated",
            "milk-livered",
            "motley-minded",
            "onion-eyed",
            "plume-plucked",
            "pottle-deep",
            "pox-marked",
            "reeling-ripe",
            "rough-hewn",
            "rude-growing",
            "rump-fed",
            "shard-borne",
            "sheep-biting",
            "spur-galled",
            "swag-bellied",
            "tardy-gaited",
            "tickle-brained",
            "toad-spotted",
            "unchin -snouted",
            "weather-bitten"};
    static final String[] column3 = {"apple-john",
            "baggage",
            "barnacle",
            "bladder",
            "boar-pig",
            "bugbear",
            "bum-bailey",
            "canker-blossom",
            "clack-dish",
            "clotpole",
            "coxcomb",
            "codpiece",
            "death-token",
            "dewberry",
            "flap-dragon",
            "flax-wench",
            "flirt-gill",
            "foot-licker",
            "fustilarian",
            "giglet",
            "gudgeon",
            "haggard",
            "harpy",
            "hedge-pig",
            "horn-beast",
            "hugger-mugger",
            "joithead",
            "lewdster",
            "lout",
            "maggot-pie",
            "malt-worm",
            "mammet",
            "measle",
            "minnow",
            "miscreant",
            "moldwarp",
            "mumble-news",
            "nut-hook",
            "pigeon-egg",
            "pignut",
            "puttock",
            "pumpion",
            "ratsbane",
            "scut",
            "skainsmate",
            "strumpet",
            "varlet",
            "vassal",
            "whey-face",
            "wagtail"};


}