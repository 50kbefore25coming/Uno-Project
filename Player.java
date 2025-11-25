/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

import java.util.*;

public class Player {
    String name;
    //Hands
    ArrayList<UnoCard> hand=new ArrayList<>();

    public Player(String name){
        this.name=name;
    }

    public ArrayList<UnoCard> getHand(){
        return hand;
    }
    
    public void firstDraw(Deck deck){
        for (int i=0;i<7;i++){
            hand.add(deck.drawCard());
        }
    }
    public void draw(UnoCard newCard){
        hand.add(newCard);
    }
    public void sortHandByColors(){
        Collections.sort(hand, (c1,c2)->{
            if (c1.getMau() == c2.getMau()){
                return c1.getSo() - c2.getSo();
            }
            else{
                 return c1.getMau().compareTo(c2.getMau());
            }
        });
    }
    public void sortHandByType(){
        Collections.sort(hand,(c1,c2)->{
            if (c1.getLoai() == c2.getLoai()){
                return c1.getMau().compareTo(c2.getMau());
            }
            else{
                 return c1.getLoai().compareTo(c2.getLoai());
            }
        });
    }
    public UnoCard playCard(int index, UnoCard currentCard){
        boolean isValid=false;
        //Check whether valid or not
        //if it is a change color wild card, can be placed on top of number cards
        if(hand.get(index).getLoai()==UnoCard.Loai.WILD&&currentCard.isNumber()){
            isValid=true;
        }
        //if it is a wild draw four card, can be placed on top of number cards, draw two cards
        if(hand.get(index).getLoai()==UnoCard.Loai.WILD_DRAW_FOUR&&(currentCard.getLoai()==UnoCard.Loai.DRAW_TWO||currentCard.isNumber())){
            isValid=true;
        }
        //if it is an action card, can be placed on top of number cards with the same color or on the same action type
        if(((hand.get(index).getMau()==currentCard.getMau())&&currentCard.isNumber())||hand.get(index).getLoai()==currentCard.getLoai()){
            isValid=true;
        }
        //if it is a number card, can be placed on top of number cards with the same color or same number
        if ((hand.get(index).getMau()==currentCard.getMau()||hand.get(index).getSo()==currentCard.getSo())&&hand.get(index).isNumber()&&currentCard.isNumber()){
            isValid=true;
        }
        if (!isValid){
            System.out.println("Invalid card");
            return null;
        }
        UnoCard temp=hand.get(index);        
        hand.remove(index);
        return temp;
    }
    @Override
    public String toString(){
        String res="";
        for (UnoCard i: hand){
            res+=i.getFullName()+" ";
        }
        return res;
    }
    // TEST đơn giản
    public static void main(String[] args) {
        Deck d=new Deck();
        d.Spawn();
        d.shuffle();
        Player p=new Player("Player");

    }
    
}
