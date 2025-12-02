/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */
package main;

import java.util.*;

public class Bot extends Player{
    //Choose type of bot algorithm
    public int botAlgorithm=1;
    
    public Bot(String name, int botAlgorithm){
        super(name);
        this.botAlgorithm=botAlgorithm;
    }
    public int getBotAlgorithm(){
        return botAlgorithm;
    }
    public void setBotAlgorithm(int botAlgorithm){
        this.botAlgorithm=botAlgorithm;
    }   
    public UnoCard playBotCard(UnoCard currentCard){
        //Check if bot has any valid cards, if return null, draw
        if(!hasValidCard(currentCard)){
            return null;
        }
        int index=-1;
        //Check which algorithm to use
        switch (botAlgorithm){
            case 1:
                index=BotAlgorithm1(currentCard);
                break;
            case 2:
                index=BotAlgorithm2(currentCard);
                break;
            case 3:
                index=BotAlgorithm3(currentCard);
                break;
            case 4:
                index=BotAlgorithm4(currentCard);
                break;
            case 5:
                index=BotAlgorithm5(currentCard);
                break;
        }
        if (index!=-1){
            UnoCard temp=hand.get(index);
            //If wild, choose random color
            if (temp.isWild()) {
                UnoCard.Mau chosenColor = randomColor();   
                temp.setMau(chosenColor);
            }    
            hand.remove(index);
            System.out.println(name+" played "+temp.getFullName());
            //Check win condition
            if (hand.size()==0){
                hasWon=true;
            }           
            return temp;
            
        }
        return null;
    }  
    //Random color
    public UnoCard.Mau randomColor(){
        Random rand=new Random();
        UnoCard.Mau[] colors={UnoCard.Mau.RED,UnoCard.Mau.GREEN,UnoCard.Mau.BLUE,UnoCard.Mau.YELLOW};
        return colors[rand.nextInt(colors.length)];
    }
    //Simple, play the first valid card found
    public int BotAlgorithm1(UnoCard currentCard){
        System.out.println(name+" algorithm 1");
        for (int i=0;i<getHand().size();i++){
            if (checkValidCard(i, currentCard)){
                return i;
            }
        }
        return -1;
    }
    //Random valid card
    public int BotAlgorithm2(UnoCard currentCard){
        //Find all valid cards
        System.out.println(name+" algorithm 2");
        ArrayList<Integer> validCards=new ArrayList<>();
        Random rand=new Random();
        for (int i=0;i<getHand().size();i++){
            if (checkValidCard(i, currentCard)){
                validCards.add(i);
            }
        }
        //Choose at random
        if (validCards.size()>0){
            int randomIndex=rand.nextInt(validCards.size());
            return validCards.get(randomIndex);
        }
        return -1;
    }
    //Prioritize action cards, then number cards
    public int BotAlgorithm3(UnoCard currentCard){
        System.out.println(name+" algorithm 3");
        sortHandByType();
        
        for (int i=getHand().size()-1;i>=0;i--){
            if (checkValidCard(i, currentCard)){
                return i;
            }
        }
        return -1;
    }
    //Prioritize color that the bot has the most 
    public int BotAlgorithm4(UnoCard currentCard){
        System.out.println(name+" algorithm 4");
        HashMap<UnoCard.Mau, Integer> colorCount=new HashMap<>();
        ArrayList<Integer> validCards=new ArrayList<>();
        ArrayList<Integer> validColorCard=new ArrayList<>();
        Random rand=new Random();
        //Get all valid cards
        for (int i=0;i<getHand().size();i++){
            if (checkValidCard(i, currentCard)){
                validCards.add(i);
            }
        }
        if (validCards.size()==0){
            return -1;
        }
        //Count colors in hand
        for (int i: validCards){
            colorCount.put(hand.get(i).getMau(), colorCount.getOrDefault(hand.get(i).getMau(), 0)+1);
        }
        //Find most frequent color
        UnoCard.Mau mostFrequentColor=null;
        int max=0;
        for(UnoCard.Mau color:colorCount.keySet()){
            if(max<colorCount.get(color)){
                max=colorCount.get(color);
                mostFrequentColor=color;
            }
        }
        for (int i: validCards){
            if (hand.get(i).getMau()==mostFrequentColor){
                validColorCard.add(i);
            }
        }
        //Choose at random among the most frequent color
        if (validColorCard.size()>0){
            int index=rand.nextInt(validColorCard.size());
            return validColorCard.get(index);
        }
        //Choose any valid card if none match the most frequent color
        if (validCards.size()>0){
            int index=rand.nextInt(validCards.size());
            return validCards.get(index);
        }
        return -1;
    }   
    //Random between all algorithms
    public int BotAlgorithm5(UnoCard currentCard){
        System.out.println(name+" algorithm 5");
        Random rand=new Random();
        int choice=rand.nextInt(4)+1;
        switch (choice){
            case 1:
                return BotAlgorithm1(currentCard);
            case 2:
                return BotAlgorithm2(currentCard);
            case 3:
                return BotAlgorithm3(currentCard);
            case 4:
                return BotAlgorithm4(currentCard);
        }
        return -1;
    }
}
