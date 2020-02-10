package com.og.ogplus.dealerapp.view;

import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameImages {
	public static final Image THREE_CARDS_DRAGON = getImage("static/images/img_3cards_dragon.png");
	public static final Image THREE_CARDS_PHOENIX = getImage("static/images/img_3cards_phoenix.png");
	public static final Image THREE_CARDS_TIE = getImage("static/images/img_tie.png");

    public static final Image CARD_SLOT = getImage("static/images/card_slot.png");
    public static final Image CARD_SLOT_ACTIVE = getImage("static/images/card_slot_active.png");
    public static final Image CARD_SLOT_DOWN = getImage("static/images/card_face_down.png");
    public static final Image BG_IMAGE = getImage("static/images/bg.png");
    public static final Image BG_IMAGE_2 = getImage("static/images/bg2.png");
    public static final Image BG_IMAGE_3 = getImage("static/images/bg3.png");
    public static final Image LOGO = getImage("static/images/logo.png");
    public static final Image CONFIRM = getImage("static/images/confirm-btn.png");
    public static final Image CANCEL = getImage("static/images/cancel-btn.png");
    public static final Image START_NEW = getImage("static/images/start-btn.png");
    public static final Image CONTINUE = getImage("static/images/continue-btn.png");
    public static final Image START_NEW_2 = getImage("static/images/start-btn-labeled.png");
    public static final Image CONTINUE_2 = getImage("static/images/continue-btn-labeled.png");
    public static final Image CLEAR = getImage("static/images/backspace.png");
    public static final Image SEND = getImage("static/images/send.png");
    public static final Image KEYPAD = getImage("static/images/keypad.png");
    public static final Image AUTO = getImage("static/images/auto.png");
    public static final Image NOT_AUTO = getImage("static/images/not-auto.png");

    public static final Image BUTTON_1 = getImage("static/images/btn1.png");
    public static final Image BUTTON_2 = getImage("static/images/btn2.png");
    public static final Image BUTTON_5 = getImage("static/images/btn5.png");
    public static final Image BUTTON_10 = getImage("static/images/btn10.png");
    public static final Image BUTTON_20 = getImage("static/images/btn20.png");
    public static final Image BUTTON_OG = getImage("static/images/btnog.png");
    public static final Image BUTTON_X3 = getImage("static/images/btnx3.png");

    public static final Image FANTAN = getImage("static/images/fantan.png");
    public static final Image FANTAN_1 = getImage("static/images/round1.png");
    public static final Image FANTAN_2 = getImage("static/images/round2.png");
    public static final Image FANTAN_3 = getImage("static/images/round3.png");
    public static final Image FANTAN_4 = getImage("static/images/round4.png");

    public static final Image ROADMAP_PLAYER = getImage("static/images/img_player.png");
    public static final Image ROADMAP_BANKER = getImage("static/images/img_banker.png");
    public static final Image ROADMAP_TIE = getImage("static/images/img_tie.png");
    public static final Image ROADMAP_DRAGON = getImage("static/images/img_dragon.png");
    public static final Image ROADMAP_TIGER = getImage("static/images/img_tiger.png");

    public static final Image BNIU9 = getImage("static/images/bniu9.png");
    public static final Image BNIU8 = getImage("static/images/bniu8.png");
    public static final Image BNIU7 = getImage("static/images/bniu7.png");
    public static final Image BNIU6 = getImage("static/images/bniu6.png");
    public static final Image BNIU5 = getImage("static/images/bniu5.png");
    public static final Image BNIU4 = getImage("static/images/bniu4.png");
    public static final Image BNIU3 = getImage("static/images/bniu3.png");
    public static final Image BNIU2 = getImage("static/images/bniu2.png");
    public static final Image BNIU1 = getImage("static/images/bniu1.png");
    public static final Image BNIUNIU = getImage("static/images/bniuniu.png");
    public static final Image BNONIU = getImage("static/images/bnoniu.png");

    public static final Image PLNIU9 = getImage("static/images/plniu9.png");
    public static final Image PLNIU8 = getImage("static/images/plniu8.png");
    public static final Image PLNIU7 = getImage("static/images/plniu7.png");
    public static final Image PLNIU6 = getImage("static/images/plniu6.png");
    public static final Image PLNIU5 = getImage("static/images/plniu5.png");
    public static final Image PLNIU4 = getImage("static/images/plniu4.png");
    public static final Image PLNIU3 = getImage("static/images/plniu3.png");
    public static final Image PLNIU2 = getImage("static/images/plniu2.png");
    public static final Image PLNIU1 = getImage("static/images/plniu1.png");
    public static final Image PLNIUNIU = getImage("static/images/plniuniu.png");
    public static final Image PLNONIU = getImage("static/images/plnoniu.png");

    public static final Image PWNIU9 = getImage("static/images/pwniu9.png");
    public static final Image PWNIU8 = getImage("static/images/pwniu8.png");
    public static final Image PWNIU7 = getImage("static/images/pwniu7.png");
    public static final Image PWNIU6 = getImage("static/images/pwniu6.png");
    public static final Image PWNIU5 = getImage("static/images/pwniu5.png");
    public static final Image PWNIU4 = getImage("static/images/pwniu4.png");
    public static final Image PWNIU3 = getImage("static/images/pwniu3.png");
    public static final Image PWNIU2 = getImage("static/images/pwniu2.png");
    public static final Image PWNIU1 = getImage("static/images/pwniu1.png");
    public static final Image PWNIUNIU = getImage("static/images/pwniuniu.png");
    public static final Image PWNONIU = getImage("static/images/pwnoniu.png");


    public static final Image BANKER = getImage("static/images/banker.png");
    public static final Image PLAYER1 = getImage("static/images/player1.png");
    public static final Image PLAYER2 = getImage("static/images/player2.png");
    public static final Image PLAYER3 = getImage("static/images/player3.png");

    public static final Image SIC_1 = getImage("static/images/dice1.png");
    public static final Image SIC_2 = getImage("static/images/dice2.png");
    public static final Image SIC_3 = getImage("static/images/dice3.png");
    public static final Image SIC_4 = getImage("static/images/dice4.png");
    public static final Image SIC_5 = getImage("static/images/dice5.png");
    public static final Image SIC_6 = getImage("static/images/dice6.png");

    public static final Image POKER = getImage("static/images/poker.jpg");
    public static final Image POKER_TABLE = getImage("static/images/poker-table.png");

    public static final Image PASSWORD_ICON = getImage("static/images/icon_password.png");
    public static final Image KEYBOARD_ICON = getImage("static/images/icon_keyboard.jpg");

    public static final Image CARD_BORDER = getImage("static/images/card_border.png");

    public static final Map<String, Image> GAME_CARDS;

    static {
        GAME_CARDS = new HashMap<>();

        //CLUBS
        GAME_CARDS.put("AC", getImage("static/images/AC.png"));
        GAME_CARDS.put("2C", getImage("static/images/2C.png"));
        GAME_CARDS.put("3C", getImage("static/images/3C.png"));
        GAME_CARDS.put("4C", getImage("static/images/4C.png"));
        GAME_CARDS.put("5C", getImage("static/images/5C.png"));
        GAME_CARDS.put("6C", getImage("static/images/6C.png"));
        GAME_CARDS.put("7C", getImage("static/images/7C.png"));
        GAME_CARDS.put("8C", getImage("static/images/8C.png"));
        GAME_CARDS.put("9C", getImage("static/images/9C.png"));
        GAME_CARDS.put("10C", getImage("static/images/10C.png"));
        GAME_CARDS.put("JC", getImage("static/images/JC.png"));
        GAME_CARDS.put("QC", getImage("static/images/QC.png"));
        GAME_CARDS.put("KC", getImage("static/images/KC.png"));
        //DIAMONDS
        GAME_CARDS.put("AD", getImage("static/images/AD.png"));
        GAME_CARDS.put("2D", getImage("static/images/2D.png"));
        GAME_CARDS.put("3D", getImage("static/images/3D.png"));
        GAME_CARDS.put("4D", getImage("static/images/4D.png"));
        GAME_CARDS.put("5D", getImage("static/images/5D.png"));
        GAME_CARDS.put("6D", getImage("static/images/6D.png"));
        GAME_CARDS.put("7D", getImage("static/images/7D.png"));
        GAME_CARDS.put("8D", getImage("static/images/8D.png"));
        GAME_CARDS.put("9D", getImage("static/images/9D.png"));
        GAME_CARDS.put("10D", getImage("static/images/10D.png"));
        GAME_CARDS.put("JD", getImage("static/images/JD.png"));
        GAME_CARDS.put("QD", getImage("static/images/QD.png"));
        GAME_CARDS.put("KD", getImage("static/images/KD.png"));
        //HEARTS
        GAME_CARDS.put("AH", getImage("static/images/AH.png"));
        GAME_CARDS.put("2H", getImage("static/images/2H.png"));
        GAME_CARDS.put("3H", getImage("static/images/3H.png"));
        GAME_CARDS.put("4H", getImage("static/images/4H.png"));
        GAME_CARDS.put("5H", getImage("static/images/5H.png"));
        GAME_CARDS.put("6H", getImage("static/images/6H.png"));
        GAME_CARDS.put("7H", getImage("static/images/7H.png"));
        GAME_CARDS.put("8H", getImage("static/images/8H.png"));
        GAME_CARDS.put("9H", getImage("static/images/9H.png"));
        GAME_CARDS.put("10H", getImage("static/images/10H.png"));
        GAME_CARDS.put("JH", getImage("static/images/JH.png"));
        GAME_CARDS.put("QH", getImage("static/images/QH.png"));
        GAME_CARDS.put("KH", getImage("static/images/KH.png"));
        //SPADES
        GAME_CARDS.put("AS", getImage("static/images/AS.png"));
        GAME_CARDS.put("2S", getImage("static/images/2S.png"));
        GAME_CARDS.put("3S", getImage("static/images/3S.png"));
        GAME_CARDS.put("4S", getImage("static/images/4S.png"));
        GAME_CARDS.put("5S", getImage("static/images/5S.png"));
        GAME_CARDS.put("6S", getImage("static/images/6S.png"));
        GAME_CARDS.put("7S", getImage("static/images/7S.png"));
        GAME_CARDS.put("8S", getImage("static/images/8S.png"));
        GAME_CARDS.put("9S", getImage("static/images/9S.png"));
        GAME_CARDS.put("10S", getImage("static/images/10S.png"));
        GAME_CARDS.put("JS", getImage("static/images/JS.png"));
        GAME_CARDS.put("QS", getImage("static/images/QS.png"));
        GAME_CARDS.put("KS", getImage("static/images/KS.png"));
    }

    public static final Map<String, Image> NIUNIU_GAME_CARDS;

    static {
        NIUNIU_GAME_CARDS = new HashMap<>();

        //CLUBS
        NIUNIU_GAME_CARDS.put("AC", getImage("static/images/niuniu/AC.png"));
        NIUNIU_GAME_CARDS.put("2C", getImage("static/images/niuniu/2C.png"));
        NIUNIU_GAME_CARDS.put("3C", getImage("static/images/niuniu/3C.png"));
        NIUNIU_GAME_CARDS.put("4C", getImage("static/images/niuniu/4C.png"));
        NIUNIU_GAME_CARDS.put("5C", getImage("static/images/niuniu/5C.png"));
        NIUNIU_GAME_CARDS.put("6C", getImage("static/images/niuniu/6C.png"));
        NIUNIU_GAME_CARDS.put("7C", getImage("static/images/niuniu/7C.png"));
        NIUNIU_GAME_CARDS.put("8C", getImage("static/images/niuniu/8C.png"));
        NIUNIU_GAME_CARDS.put("9C", getImage("static/images/niuniu/9C.png"));
        NIUNIU_GAME_CARDS.put("10C", getImage("static/images/niuniu/10C.png"));
        NIUNIU_GAME_CARDS.put("JC", getImage("static/images/niuniu/JC.png"));
        NIUNIU_GAME_CARDS.put("QC", getImage("static/images/niuniu/QC.png"));
        NIUNIU_GAME_CARDS.put("KC", getImage("static/images/niuniu/KC.png"));
        //DIAMONDS
        NIUNIU_GAME_CARDS.put("AD", getImage("static/images/niuniu/AD.png"));
        NIUNIU_GAME_CARDS.put("2D", getImage("static/images/niuniu/2D.png"));
        NIUNIU_GAME_CARDS.put("3D", getImage("static/images/niuniu/3D.png"));
        NIUNIU_GAME_CARDS.put("4D", getImage("static/images/niuniu/4D.png"));
        NIUNIU_GAME_CARDS.put("5D", getImage("static/images/niuniu/5D.png"));
        NIUNIU_GAME_CARDS.put("6D", getImage("static/images/niuniu/6D.png"));
        NIUNIU_GAME_CARDS.put("7D", getImage("static/images/niuniu/7D.png"));
        NIUNIU_GAME_CARDS.put("8D", getImage("static/images/niuniu/8D.png"));
        NIUNIU_GAME_CARDS.put("9D", getImage("static/images/niuniu/9D.png"));
        NIUNIU_GAME_CARDS.put("10D", getImage("static/images/niuniu/10D.png"));
        NIUNIU_GAME_CARDS.put("JD", getImage("static/images/niuniu/JD.png"));
        NIUNIU_GAME_CARDS.put("QD", getImage("static/images/niuniu/QD.png"));
        NIUNIU_GAME_CARDS.put("KD", getImage("static/images/niuniu/KD.png"));
        //HEARTS
        NIUNIU_GAME_CARDS.put("AH", getImage("static/images/niuniu/AH.png"));
        NIUNIU_GAME_CARDS.put("2H", getImage("static/images/niuniu/2H.png"));
        NIUNIU_GAME_CARDS.put("3H", getImage("static/images/niuniu/3H.png"));
        NIUNIU_GAME_CARDS.put("4H", getImage("static/images/niuniu/4H.png"));
        NIUNIU_GAME_CARDS.put("5H", getImage("static/images/niuniu/5H.png"));
        NIUNIU_GAME_CARDS.put("6H", getImage("static/images/niuniu/6H.png"));
        NIUNIU_GAME_CARDS.put("7H", getImage("static/images/niuniu/7H.png"));
        NIUNIU_GAME_CARDS.put("8H", getImage("static/images/niuniu/8H.png"));
        NIUNIU_GAME_CARDS.put("9H", getImage("static/images/niuniu/9H.png"));
        NIUNIU_GAME_CARDS.put("10H", getImage("static/images/niuniu/10H.png"));
        NIUNIU_GAME_CARDS.put("JH", getImage("static/images/niuniu/JH.png"));
        NIUNIU_GAME_CARDS.put("QH", getImage("static/images/niuniu/QH.png"));
        NIUNIU_GAME_CARDS.put("KH", getImage("static/images/niuniu/KH.png"));
        //SPADES
        NIUNIU_GAME_CARDS.put("AS", getImage("static/images/niuniu/AS.png"));
        NIUNIU_GAME_CARDS.put("2S", getImage("static/images/niuniu/2S.png"));
        NIUNIU_GAME_CARDS.put("3S", getImage("static/images/niuniu/3S.png"));
        NIUNIU_GAME_CARDS.put("4S", getImage("static/images/niuniu/4S.png"));
        NIUNIU_GAME_CARDS.put("5S", getImage("static/images/niuniu/5S.png"));
        NIUNIU_GAME_CARDS.put("6S", getImage("static/images/niuniu/6S.png"));
        NIUNIU_GAME_CARDS.put("7S", getImage("static/images/niuniu/7S.png"));
        NIUNIU_GAME_CARDS.put("8S", getImage("static/images/niuniu/8S.png"));
        NIUNIU_GAME_CARDS.put("9S", getImage("static/images/niuniu/9S.png"));
        NIUNIU_GAME_CARDS.put("10S", getImage("static/images/niuniu/10S.png"));
        NIUNIU_GAME_CARDS.put("JS", getImage("static/images/niuniu/JS.png"));
        NIUNIU_GAME_CARDS.put("QS", getImage("static/images/niuniu/QS.png"));
        NIUNIU_GAME_CARDS.put("KS", getImage("static/images/niuniu/KS.png"));
    }

    private static Image getImage(String path) {
        try {
            return ImageIO.read(new ClassPathResource(path).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Image can't be found in " + path);
        }
    }
}
