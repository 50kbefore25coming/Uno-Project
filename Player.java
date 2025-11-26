/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

import java.util.*;

public class Player {
    String name;
    boolean hasWon=false;   
    //Hands
    ArrayList<UnoCard> hand=new ArrayList<>();
    //Constructor and getter setter
    public Player(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public ArrayList<UnoCard> getHand(){
        return hand;
    }
    public void setHand(ArrayList<UnoCard> hand){
        this.hand=hand;
    }
    //Check whether player has won or not
    public boolean getState(){
        return hasWon;
    }
    public void setState(boolean state){
        this.hasWon=state;
    }
    //Check if player has any valid cards to play, if return false, must draw
    public boolean hasValidCard(UnoCard currentCard){
        for (int i=0;i<hand.size();i++){
            if (checkValidCard(i,currentCard)){
                return true;
            }
        }
        return false;
    }
    //Draw 7 cards at the beginning of the game
    public void firstDraw(Deck deck){
        for (int i=0;i<7;i++){
            hand.add(deck.drawCard());
        }
    }
    //Draw a card
    public void draw(UnoCard newCard){
        hand.add(newCard);
    }
    //Sort hand by colors
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
    //Sort hand by type
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
    //Play a card from hand, return the played card
    public UnoCard playCard(int index, UnoCard currentCard){
        //Check valid card
        if (!checkValidCard(index,currentCard)){
            System.out.println("Invalid card");
            return null;
        }
        UnoCard temp=hand.get(index);        
        hand.remove(index);
        System.out.println(name+" played "+temp.getFullName());
        //Check win condition
        if (hand.size()==0){
            hasWon=true;
        }
        return temp;
    }
    //Check whether a card is valid to play
    public boolean checkValidCard(int index, UnoCard currentCard){
        if (index<0||index>=hand.size()){
            System.out.println("Index out of bounds");
            return false;
        }
        UnoCard card=hand.get(index);
        //Check whether valid or not
        //First card of the game, dont need to check
        if (currentCard == null){
            return true;
        }
        //Use the matches method from UnoCard to check validity
        //Pass the currentCard's mau which includes any color updated by setMau()
        return card.matches(currentCard, currentCard.getMau());
    }
    @Override
    public String toString(){
        String res=name+"'s hand: ";
        for (UnoCard i: hand){
            res+=i.getFullName()+" ";
        }
        return res;
    }
    //TEST
    public static void main(String[] args) {
        Deck d=new Deck();
        d.Spawn();
        d.shuffle();
        Player p=new Player("Player");
        p.firstDraw(d);
        p.sortHandByType();
        System.out.println(p);
    }
    
}
