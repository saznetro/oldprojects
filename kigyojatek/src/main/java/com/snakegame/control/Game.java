package com.snakegame.control;

import java.util.LinkedList;

import com.snakegame.BodyPartType;
import com.snakegame.Direction;
import com.snakegame.FoodTypes;
import com.snakegame.GameState;
import com.snakegame.Util;
import com.snakegame.model.Snake;
import com.snakegame.model.SnakeBodyPart;

/**
 * The game representation. Discribes the connection between food and snake.
 *
 */
public class Game {

    private static final int BONUS_FOOD_SCORE = 20;
    private static final int DEFAULT_SCORE_RISE = 10;
    private Snake snake;
    private byte tablesize;
    private int score;
    private boolean isScoreRised;
    private boolean eaten;
    private Direction directionOfTheNextStep;
    private SnakeBodyPart newpart;
    private LinkedList<SnakeBodyPart> foodList;
    private LinkedList<SnakeBodyPart> eatenFood;
    private GameState gameover;

    /**
     * Constructor.
     */
    public Game() {
        super();
        isScoreRised = eaten = false;
        gameover = GameState.IN_GAME;
        score = tablesize = 0;
        newpart = new SnakeBodyPart((byte) 5, (byte) 8);

        foodList = new LinkedList<SnakeBodyPart>();
        eatenFood = new LinkedList<SnakeBodyPart>();
        foodList.add(new SnakeBodyPart((byte) 5, (byte) 8));
    }

    /**
     * Initialize again
     */
    public void reInitialize() {

        foodList = new LinkedList<SnakeBodyPart>();
        eatenFood = new LinkedList<SnakeBodyPart>();
        foodList.add(new SnakeBodyPart((byte) 5, (byte) 8));
        score = 0;
        gameover = GameState.IN_GAME;
    }

    /**
     * Make a snake step happen
     */
    public void doStep() {
        snake.doStep(directionOfTheNextStep);
        eatFood();
    }

    /***
     * Check if there is next step.
     * 
     * @return true if there is, false otherwise
     */
    public boolean isThereNextStep() {
        return snake.isThereNextStep(newpart, tablesize);
    }

    /**
     * Eat the food.
     * 
     * @return
     */
    public boolean eatFood() {
        int whichfood = snake.getHead().isAt(foodList);
        SnakeBodyPart selectedFood;
        if (whichfood <= -1) {
            return false;
        }
        selectedFood = foodList.get(whichfood);
        selectedFood.setDirection(snake.getTail().getDirection());
        eatenFood.addFirst(
                new SnakeBodyPart(selectedFood.getXCoordinate(), selectedFood.getYCoordinate(), BodyPartType.NEWPART));
        if (whichfood == FoodTypes.NORMAL.ordinal()) {
            newFoodsPlace(FoodTypes.NORMAL);
        } else if (whichfood == FoodTypes.BONUS.ordinal()) {
            foodList.removeLast();
        }
        increaseScore();
        addBonusFood();
        setEaten(true);

        return true;

    }

    private void addBonusFood() {
        if (score == BONUS_FOOD_SCORE) {
            foodList.addLast(new SnakeBodyPart(snake.getTail().getXCoordinate(), snake.getTail().getYCoordinate()));
            newFoodsPlace(FoodTypes.BONUS);

        }
    }

    /**
     * Increase the score
     */
    public void increaseScore() {
        score += DEFAULT_SCORE_RISE;
        setScoreRised(true);

    }

    /***
     * Generate a new food place
     * 
     * @param which
     *            the type of the food (0 normal, 1 bonus)
     */
    public void newFoodsPlace(FoodTypes foodtype) {

        boolean collison = false;
        do {
            SnakeBodyPart snakeBodyPart = foodList.get(foodtype.ordinal());
            snakeBodyPart.setState(Util.random((byte) 0, tablesize), Util.random((byte) 0, tablesize), null);
            collison = snake.isCoordinatesOnSnake(snakeBodyPart.getXCoordinate(), snakeBodyPart.getYCoordinate());
            if (collison) {
                continue;
            }
            for (int k = 0; k < foodList.size(); k++) {
                if (k == foodtype.ordinal())
                    continue;
                if (foodList.get(k).isAtGivenCoordinate(snakeBodyPart)) {
                    collison = true;
                    break;
                }
            }
        } while (collison);
    }

    /***
     * if the the food at the tail we remove it.
     */
    public void eatenFoodAtTail() {
        SnakeBodyPart previousTail = snake.getTail();
        int whichfood = previousTail.isAt(eatenFood);
        if (whichfood <= -1) {
            return;
        }
        snake.addTail(eatenFood.get(whichfood));
        eatenFood.remove(whichfood);
        snake.getTail().setPartType(BodyPartType.TAIL);
        previousTail.setPartType(BodyPartType.TORSO);
        snake.getTail().setDirection(previousTail.getDirection());

    }

    /**
     * Set the snake
     * 
     * @param snake
     */
    public void setSnake(Snake snake) {
        this.snake = snake;
    }

    /***
     * Get the snake
     * 
     * @return
     */
    public Snake getSnake() {
        return snake;
    }

    /**
     * Get the score
     * 
     * @return
     */
    public int getScore() {
        return score;
    }

    /***
     * Set the direction
     * 
     * @param direction
     */
    public void setDirection(Direction direction) {
        this.directionOfTheNextStep = direction;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void setScoreRised(boolean scorerised) {
        this.isScoreRised = scorerised;
    }

    /***
     * Check if the score has rised
     * 
     * @return true if yes, otherwise false
     */
    public boolean isScorerised() {
        return isScoreRised;
    }

    /**
     * Set the table size
     * 
     * @param tableSize
     */
    public void setTableSize(byte tableSize) {
        this.tablesize = tableSize;
    }

    public LinkedList<SnakeBodyPart> getFoodList() {
        return foodList;
    }

    public LinkedList<SnakeBodyPart> getEatenFood() {
        return eatenFood;
    }

    /***
     * Set the game state
     * 
     * @param gameover
     */
    public void setGameState(GameState gameover) {
        this.gameover = gameover;
    }

}
