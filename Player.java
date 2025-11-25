/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

import java.util.*;

public class Player {
    int cardCount=0;
    ArrayList<UnoCard> hand=new ArrayList<>();
    
    public ArrayList<UnoCard> getHand(){
        return hand;
    }
    public void firstDraw(Deck deck){
        for (int i=0;i<7;i++){
            hand.add(deck.drawCard());
        }
        cardCount=hand.size();
    }
    public void draw(UnoCard newCard){
        hand.add(newCard);
        cardCount=hand.size();
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
    public UnoCard playCard(int index){
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
        Player p=new Player();

    }
    
}
