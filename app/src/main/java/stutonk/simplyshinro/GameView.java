package stutonk.simplyshinro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import shinro.GridPos;
import shinro.ShinroPuzzle;

/**
 * This is where all the magic of the game happens and it deserves thorough documentation which I
 * plan to do when there's more time.
 * <p>
 * Copyright 2015 Joseph Eib
 * </p>
 * @author Joseph Eib
 * @since January 2015
 */
public class GameView extends View {

    //C-style datatype for simplicity
    private class Coord {
        float x, y;
    }

    private static final int PUZZSIZE = ShinroPuzzle.SIZE;
    private static final int NUMLINES = PUZZSIZE + 1;

    private int mHeight, mWidth;
    private int gridSize, actualGridSize, squareSize;
    private float stroke, fatStroke, bigStroke, lineOffset, xOffset, yOffset, cornerOffset;
    private boolean puzzleSolved, puzzleFull;
    private Coord[][] corners;
    private Canvas compositor;
    private Bitmap gameBitmap, squareBitmap, bigSquareBitmap;
    private Paint blankPaint, backgroundPaint, gridPaint, gridHighlightPaint, pointPaint,
            pointHighlightPaint, xPaint, xHighlightPaint, victoryPaint, defeatPaint, textPaint,
            difficultyTextPaint;
    private Typeface robotoThin;
    private Context mContext;

    //to support this view's puzzle
    Puzzle puzzle;
    ShinroPuzzle shinroPuzzle;
    private boolean[][] highlight;
    int[] numFilledRow, numFilledCol;


    //Constructors and Methods
    public GameView(Context context) {
        super(context);
        mContext = context;
    }

    public GameView(Context context, Puzzle puzzle) {
        super(context);
        mContext = context;
        this.puzzle = puzzle;
        shinroPuzzle = puzzle.getShinroPuzzle();

        robotoThin = Typeface.createFromAsset(context.getAssets(), "Roboto-Thin.ttf");
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        //the order of these is somewhat important
        puzzleSolved = false;
        puzzleFull = false;
        stroke = Math.round(mWidth / 350f);
        fatStroke = stroke * 10;
        lineOffset = mWidth / (NUMLINES + 1f);
        xOffset = mWidth / (2f * (NUMLINES + 1f));
        yOffset = (mHeight / 2f) - (mWidth / 2f);
        squareSize = (int)(lineOffset - (stroke * 2));
        cornerOffset = lineOffset / 12;
        gridSize = (int) (lineOffset * NUMLINES);
        actualGridSize = (int) (Math.ceil(lineOffset * NUMLINES) + (stroke * 2));
        bigStroke = gridSize * (stroke / squareSize);

        corners = new Coord[NUMLINES][NUMLINES];

        initPaints();
        initBitmaps();

        drawGrid();
        initPuzzle();

        invalidate();
    }

    private void initBitmaps() {
        //Create the empty square
        squareBitmap = Bitmap.createBitmap(squareSize, squareSize, Bitmap.Config.ARGB_8888);
        compositor = new Canvas(squareBitmap);
        compositor.drawPaint(backgroundPaint);

        //create big empty square
        bigSquareBitmap = Bitmap.createBitmap(actualGridSize, actualGridSize,
                Bitmap.Config.ARGB_8888);
        compositor = new Canvas(bigSquareBitmap);
        compositor.drawPaint(backgroundPaint);

        //Create the gameBitmap
        gameBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        compositor = new Canvas(gameBitmap);
        compositor.drawPaint(backgroundPaint);

    }

    private void initPaints() {
        blankPaint = new Paint();

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setColor(getResources().getColor(R.color.charcoal));

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.silver));
        textPaint.setTypeface(robotoThin);
        textPaint.setTextSize(mHeight / 20);
        textPaint.setTextAlign(Paint.Align.CENTER);

        difficultyTextPaint = new Paint();
        difficultyTextPaint.setAntiAlias(true);
        difficultyTextPaint.setColor(Puzzle.getDifficultyColor(puzzle.getDifficulty()));
        difficultyTextPaint.setTypeface(robotoThin);
        difficultyTextPaint.setTextSize(mHeight / 25);
        difficultyTextPaint.setTextAlign(Paint.Align.CENTER);

        gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(stroke);
        gridPaint.setStrokeCap(Paint.Cap.SQUARE);
        gridPaint.setStrokeJoin(Paint.Join.MITER);
        gridPaint.setColor(getResources().getColor(R.color.smoke));

        gridHighlightPaint = new Paint();
        gridHighlightPaint.setAntiAlias(true);
        gridHighlightPaint.setStyle(Paint.Style.STROKE);
        gridHighlightPaint.setStrokeWidth(fatStroke);
        gridHighlightPaint.setStrokeCap(Paint.Cap.SQUARE);
        gridHighlightPaint.setStrokeJoin(Paint.Join.MITER);
        gridHighlightPaint.setColor(getResources().getColor(R.color.smoke));

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(stroke);
        pointPaint.setStrokeCap(Paint.Cap.SQUARE);
        pointPaint.setStrokeJoin(Paint.Join.MITER);
        pointPaint.setColor(getResources().getColor(R.color.star_bright_blue));

        pointHighlightPaint = new Paint();
        pointHighlightPaint.setAntiAlias(true);
        pointHighlightPaint.setStyle(Paint.Style.STROKE);
        pointHighlightPaint.setStrokeWidth(fatStroke);
        pointHighlightPaint.setStrokeCap(Paint.Cap.SQUARE);
        pointHighlightPaint.setStrokeJoin(Paint.Join.MITER);
        pointHighlightPaint.setColor(getResources().getColor(R.color.star_blue));

        xPaint = new Paint();
        xPaint.setAntiAlias(true);
        xPaint.setStyle(Paint.Style.STROKE);
        xPaint.setStrokeWidth(stroke);
        xPaint.setStrokeCap(Paint.Cap.SQUARE);
        xPaint.setStrokeJoin(Paint.Join.MITER);
        xPaint.setColor(getResources().getColor(R.color.bright_blood));
        xPaint.setTextSize(mHeight / 20);
        xPaint.setTypeface(robotoThin);
        xPaint.setTextAlign(Paint.Align.CENTER);

        xHighlightPaint = new Paint();
        xHighlightPaint.setAntiAlias(true);
        xHighlightPaint.setStyle(Paint.Style.STROKE);
        xHighlightPaint.setStrokeWidth(fatStroke);
        xHighlightPaint.setStrokeCap(Paint.Cap.SQUARE);
        xHighlightPaint.setStrokeJoin(Paint.Join.MITER);
        xHighlightPaint.setColor(getResources().getColor(R.color.blood));

        victoryPaint = new Paint();
        victoryPaint.setAntiAlias(true);
        victoryPaint.setStyle(Paint.Style.STROKE);
        victoryPaint.setStrokeWidth(bigStroke);
        victoryPaint.setStrokeCap(Paint.Cap.SQUARE);
        victoryPaint.setStrokeJoin(Paint.Join.MITER);
        victoryPaint.setColor(getResources().getColor(R.color.star_blue));

        defeatPaint = new Paint();
        defeatPaint.setAntiAlias(true);
        defeatPaint.setStyle(Paint.Style.STROKE);
        defeatPaint.setStrokeWidth(bigStroke);
        defeatPaint.setStrokeCap(Paint.Cap.SQUARE);
        defeatPaint.setStrokeJoin(Paint.Join.MITER);
        defeatPaint.setColor(getResources().getColor(R.color.blood));
    }

    private void initPuzzle() {
        highlight = new boolean[PUZZSIZE][PUZZSIZE];
        numFilledRow = new int[PUZZSIZE];
        numFilledCol = new int[PUZZSIZE];
        shinroPuzzle.reset();

        //blitNum needs to put the number of row/col i - 1 to row/col i + 1
        for (int i = 1; i < NUMLINES; i++) {
            numFilledRow[i - 1] = 0;
            numFilledCol[i - 1] = 0;
            blitNum(i, 0, shinroPuzzle.getRowHeaderNum(i - 1), numFilledRow[i - 1]);
            blitNum(0, i, shinroPuzzle.getColHeaderNum(i - 1), numFilledCol[i - 1]);
        }

        for (int row = 0; row < PUZZSIZE; row ++) {
            for (int col = 0; col < PUZZSIZE; col++) {
                int space = shinroPuzzle.atPos(row, col);
                if (space > 0 && space < 9) {
                    blitArrow(row, col, space, pointPaint);
                }
            }
        }
    }

    private void blitArrow(int row, int col, int dir, Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(squareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);

        float lineX, lineY, lineEndX, lineEndY;
        float arrowPointX, arrowPointY, arrowTipX1, arrowTipY1, arrowTipX2, arrowTipY2;
        float diagLength = squareSize - (cornerOffset * 2);
        float arrowTipLength = (float)Math.sqrt(Math.pow((squareSize / 2 - cornerOffset), 2)
                + Math.pow((squareSize / 2 - cornerOffset), 2)); //euclidean distance

        //initilizations
        lineX = lineY = lineEndX = lineEndY = 0;
        arrowPointX = arrowPointY = arrowTipX1 = arrowTipX2 = arrowTipY1 = arrowTipY2 = 0;

        //arrow lines
        if (dir == ShinroPuzzle.N || dir == ShinroPuzzle.S || dir == ShinroPuzzle.n
                || dir == ShinroPuzzle.s) {
            lineX = lineEndX = squareSize / 2;
            lineY = cornerOffset;
            lineEndY = squareSize - cornerOffset;
        }
        else if (dir == ShinroPuzzle.E || dir == ShinroPuzzle.W || dir == ShinroPuzzle.e
                || dir == ShinroPuzzle.w) {
            lineX = cornerOffset;
            lineEndX = squareSize - cornerOffset;
            lineY = lineEndY = squareSize / 2;
        }
        //TODO: move this to only be calculated once
        else if (dir == ShinroPuzzle.NW || dir == ShinroPuzzle.SE || dir == ShinroPuzzle.nw
                || dir == ShinroPuzzle.se) {
            lineX = squareSize - (squareSize * (diagLength / squareSize));
            lineEndX = squareSize * (diagLength / squareSize);
            lineY = squareSize - (squareSize * (diagLength / squareSize));
            lineEndY = squareSize * (diagLength / squareSize);
        }
        else if (dir == ShinroPuzzle.NE || dir == ShinroPuzzle.SW || dir == ShinroPuzzle.ne
                || dir == ShinroPuzzle.sw) {
            lineX = squareSize - (squareSize * (diagLength / squareSize));
            lineEndX = squareSize * (diagLength / squareSize);
            lineY = squareSize * (diagLength / squareSize);
            lineEndY = squareSize - (squareSize * (diagLength / squareSize));
        }

        /* arrow tip lines
         * note: to do short arrow tips, for diags have tip ends at the center of square
         * for nsew, arrow tip length is squaresize/4
         */
        switch (dir) {
            case ShinroPuzzle.N: //pass through
            case ShinroPuzzle.n: arrowPointX = lineX;
                                 arrowPointY = lineY;
                                 arrowTipX1 = cornerOffset;
                                 arrowTipX2 = squareSize - cornerOffset;
                                 arrowTipY1 = arrowTipY2 = squareSize / 2;
                                 break;
            case ShinroPuzzle.S: //pass through
            case ShinroPuzzle.s: arrowPointX = lineEndX;
                                 arrowPointY = lineEndY;
                                 arrowTipX1 = cornerOffset;
                                 arrowTipX2 = squareSize - cornerOffset;
                                 arrowTipY1 = arrowTipY2 = squareSize / 2;
                                 break;
            case ShinroPuzzle.E: //pass through
            case ShinroPuzzle.e: arrowPointX = lineEndX;
                                 arrowPointY = lineEndY;
                                 arrowTipX1 = arrowTipX2 = squareSize / 2;
                                 arrowTipY1 = cornerOffset;
                                 arrowTipY2 = squareSize - cornerOffset;
                                 break;
            case ShinroPuzzle.W: //pass through
            case ShinroPuzzle.w: arrowPointX = lineX;
                                 arrowPointY = lineY;
                                 arrowTipX1 = arrowTipX2 = squareSize / 2;
                                 arrowTipY1 = cornerOffset;
                                 arrowTipY2 = squareSize - cornerOffset;
                                 break;
            case ShinroPuzzle.NE: //pass through
            case ShinroPuzzle.ne: arrowPointX = lineEndX;
                                  arrowPointY = lineEndY;
                                  if (paint.getStrokeWidth() == fatStroke) {
                                      lineEndX -= fatStroke / 2;
                                      lineEndY += fatStroke / 2;
                                  }
                                  arrowTipX1 = arrowPointX - arrowTipLength;
                                  arrowTipX2 = arrowPointX;
                                  arrowTipY1 = arrowPointY;
                                  arrowTipY2 = arrowPointY + arrowTipLength;
                                  break;
            case ShinroPuzzle.NW: //pass through
            case ShinroPuzzle.nw: arrowPointX = lineX;
                                  arrowPointY = lineY;
                                  if (paint.getStrokeWidth() == fatStroke) {
                                      lineX += fatStroke / 2;
                                      lineY += fatStroke / 2;
                                  }
                                  arrowTipX1 = arrowPointX + arrowTipLength;
                                  arrowTipX2 = arrowPointX;
                                  arrowTipY1 = arrowPointY;
                                  arrowTipY2 = arrowPointY + arrowTipLength;
                                  break;
            case ShinroPuzzle.SE: //pass through
            case ShinroPuzzle.se: arrowPointX = lineEndX;
                                  arrowPointY = lineEndY;
                                  if (paint.getStrokeWidth() == fatStroke) {
                                      lineEndX -= fatStroke / 2;
                                      lineEndY -= fatStroke / 2;
                                  }
                                  arrowTipX1 = arrowPointX - arrowTipLength;
                                  arrowTipX2 = arrowPointX;
                                  arrowTipY1 = arrowPointY;
                                  arrowTipY2 = arrowPointY - arrowTipLength;
                                  break;
            case ShinroPuzzle.SW: //pass through
            case ShinroPuzzle.sw: arrowPointX = lineX;
                                  arrowPointY = lineY;
                                  if (paint.getStrokeWidth() == fatStroke) {
                                      lineX += fatStroke / 2;
                                      lineY -= fatStroke / 2;
                                  }
                                  arrowTipX1 = arrowPointX + arrowTipLength;
                                  arrowTipX2 = arrowPointX;
                                  arrowTipY1 = arrowPointY;
                                  arrowTipY2 = arrowPointY - arrowTipLength;
                                  break;
        }

        //draw arrow
        compositor.drawLine(lineX, lineY, lineEndX, lineEndY, paint);
        compositor.drawLine(arrowPointX, arrowPointY, arrowTipX1, arrowTipY1, paint);
        compositor.drawLine(arrowPointX, arrowPointY, arrowTipX2, arrowTipY2, paint);

        //reset the compositor to the gameBitmap and blit
        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, corners[row + 1][col + 1].x,
                corners[row + 1][col + 1].y, blankPaint);
    }

    private void blitArrow(GridPos pos, int dir, Paint paint) {
        blitArrow(pos.getRow(), pos.getCol(), dir, paint);
    }

    private void blitNum(int row, int col, int num, int numFilled) {
        int limit = 1;
        boolean odd = true;

        if (num % 2 == 0) {
            odd = false;
            limit = 0;
        }

        float rectSize = lineOffset / 5f;
        Paint paint;

        Bitmap bitmap = Bitmap.createBitmap(squareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);

        if (odd) {
            if (num == numFilled) {
                paint = pointPaint;
            }
            else if (numFilled > num) {
                paint = xPaint;
            }
            else {
                paint = gridPaint;
            }
            compositor.drawRect(squareSize / 2 - rectSize / 2, squareSize / 2 - rectSize / 2,
                    squareSize / 2 + rectSize / 2, squareSize / 2 + rectSize / 2, paint);
        }

        for (int i = 0; i < (num - limit); i++) {
            float left, right, top, bottom;
            switch (i) {
                case 0: left = top = cornerOffset;
                    right = bottom = cornerOffset + rectSize;
                    break;
                case 1: left = top = squareSize - cornerOffset - rectSize;
                    right = bottom = squareSize - cornerOffset;
                    break;
                case 2: left = cornerOffset;
                    top = squareSize - cornerOffset - rectSize;
                    right = cornerOffset + rectSize;
                    bottom = squareSize - cornerOffset;
                    break;
                case 3: left = squareSize - cornerOffset - rectSize;
                    top = cornerOffset;
                    right = squareSize - cornerOffset;
                    bottom = cornerOffset + rectSize;
                    break;
                case 4: left = cornerOffset;
                    top = squareSize / 2 - rectSize / 2;
                    right = cornerOffset + rectSize;
                    bottom = squareSize / 2 + rectSize / 2;
                    break;
                case 5: left = squareSize - cornerOffset - rectSize;
                    top = squareSize / 2 - rectSize / 2;
                    right = squareSize - cornerOffset;
                    bottom = squareSize / 2 + rectSize / 2;
                    break;
                case 6: left = squareSize / 2 - rectSize / 2;
                    top = cornerOffset;
                    right = squareSize / 2 + rectSize / 2;
                    bottom = cornerOffset + rectSize;
                    break;
                case 7: left = squareSize / 2 - rectSize / 2;
                    top = squareSize - cornerOffset - rectSize;
                    right = squareSize / 2 + rectSize / 2;
                    bottom = squareSize - cornerOffset;
                    break;
                default:left = right = top = bottom = 0;
                    break;
            }

            if (numFilled > num) {
                paint = xPaint;
            }
            else {
                if (i < numFilled) {
                    paint = pointPaint;
                } else {
                    paint = gridPaint;
                }
            }

            compositor.drawRect(left, top, right, bottom, paint);
        }

        //reset the compositor to the gameBitmap and blit
        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, corners[row][col].x, corners[row][col].y, blankPaint);
    }

    /*private void blitNum(GridPos pos, int num, int numFilled) {
        blitNum(pos.getRow(), pos.getCol(), num, numFilled);
    }*/

    private void blitPoint(int row, int col, Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(squareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);
        compositor.drawCircle(squareSize / 2, squareSize / 2,
                squareSize / 2 - (paint.getStrokeWidth() / 2), paint);

        //reset the compositor to the gameBitmap and blit
        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, corners[row + 1][col + 1].x,
                corners[row + 1][col + 1].y, blankPaint);
    }

    private void blitPoint(GridPos pos, Paint paint) {
        blitPoint(pos.getRow(), pos.getCol(), paint);
    }

    private void blitSquare(int row, int col, Paint paint) {
        float paintStroke = paint.getStrokeWidth();
        float squareOffset = paintStroke / 2 + lineOffset / 20;
        Bitmap bitmap = Bitmap.createBitmap(squareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);
        compositor.drawRect(squareOffset, squareOffset, squareSize - squareOffset,
                squareSize - squareOffset, paint);

        //reset the compositor to the gameBitmap and blit
        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, corners[row + 1][col + 1].x,
                corners[row + 1][col + 1].y, blankPaint);
    }

    private void blitSquare(GridPos pos, Paint paint) {
        blitSquare(pos.getRow(), pos.getCol(), paint);
    }

    private void blitX(int row, int col, Paint paint) {
        Bitmap bitmap = Bitmap.createBitmap(squareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);
        compositor.drawLine(0, 0, squareSize, squareSize, paint);
        compositor.drawLine(0, squareSize, squareSize, 0, paint);

        //reset the compositor to the gameBitmap and blit
        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, corners[row + 1][col + 1].x,
                corners[row + 1][col + 1].y, blankPaint);
    }

    private void blitX(GridPos pos, Paint paint) {
        blitX(pos.getRow(), pos.getCol(), paint);
    }

    private void blitVictory() {
        Bitmap bitmap = Bitmap.createBitmap(bigSquareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);
        compositor.drawCircle(actualGridSize / 2, actualGridSize / 2,
                actualGridSize / 2 - (victoryPaint.getStrokeWidth() / 2), victoryPaint);

        //reset the compositor to the gameBitmap and blit
        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, xOffset - stroke, yOffset - stroke, blankPaint);
    }

    private void blitDefeat() {
        Bitmap bitmap = Bitmap.createBitmap(bigSquareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);
        compositor.drawLine(0, 0, actualGridSize, actualGridSize, defeatPaint);
        compositor.drawLine(0, actualGridSize, actualGridSize, 0, defeatPaint);

        //reset the compositor to the gameBitmap and blit
        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, xOffset - stroke, yOffset - stroke, blankPaint);
    }

    private void blitBack() {
        Bitmap bitmap = Bitmap.createBitmap(squareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);
        //compositor.drawRect(0, 0, squareSize, squareSize, gridPaint);

        float lineX, lineEndX, lineY, lineEndY, arrowPointX, arrowPointY, arrowTipX1, arrowTipX2,
                arrowTipY1, arrowTipY2;

        lineX = squareSize / 12;
        lineEndX = squareSize - squareSize / 12;
        lineY = lineEndY = squareSize / 2;

        arrowPointX = lineX;
        arrowPointY = lineY;
        arrowTipX1 = arrowTipX2 = squareSize / 2;
        arrowTipY1 = squareSize / 12;
        arrowTipY2 = squareSize - squareSize / 12;

        compositor.drawLine(lineX, lineY, lineEndX, lineEndY, xPaint);
        compositor.drawLine(arrowPointX, arrowPointY, arrowTipX1, arrowTipY1, xPaint);
        compositor.drawLine(arrowPointX, arrowPointY, arrowTipX2, arrowTipY2, xPaint);

        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, mWidth / 4 - squareSize / 2,
                ((mHeight - gridSize - yOffset) / 2) + yOffset + gridSize - squareSize / 2,
                blankPaint);
        compositor.drawCircle(mWidth / 4,
                ((mHeight - gridSize - yOffset) / 2) + yOffset + gridSize,
                squareSize / 2 + stroke * 5, xPaint);
    }

    private void blitReset() {
        Bitmap bitmap = Bitmap.createBitmap(squareBitmap);
        compositor = new Canvas(bitmap);
        compositor.drawPaint(backgroundPaint);

        compositor.drawLine(squareSize / 12, squareSize / 12, squareSize - squareSize / 12,
                squareSize - squareSize / 12, xPaint);
        compositor.drawLine(squareSize / 12, squareSize - squareSize / 12,
                squareSize - squareSize / 12, squareSize / 12, xPaint);

        compositor = new Canvas(gameBitmap);
        compositor.drawBitmap(bitmap, mWidth / 4 * 3 - squareSize / 2,
                ((mHeight - gridSize - yOffset) / 2) + yOffset + gridSize - squareSize / 2,
                blankPaint);
        compositor.drawCircle(mWidth / 4 * 3,
                ((mHeight - gridSize - yOffset) / 2) + yOffset + gridSize,
                squareSize / 2 + stroke * 5, xPaint);
    }

    private void drawGrid() {
        //clear board
        compositor.drawPaint(backgroundPaint);

        float[] verticals = new float[NUMLINES];
        float[] horizontals = new float[NUMLINES];


        for (int i = 1; i < (NUMLINES + 1); i ++) {
            verticals[i-1] = xOffset + lineOffset * i;
            horizontals[i-1] = yOffset + lineOffset * i;
        }

        for (int row = 0; row < NUMLINES; row++) {
            for (int col = 0; col < NUMLINES; col++) {
                corners[row][col] = new Coord();
                corners[row][col].x = verticals[col] - lineOffset + stroke;
                corners[row][col].y = horizontals[row] - lineOffset + stroke;
            }
        }

        float bottom = horizontals[horizontals.length - 1];
        float right = verticals[verticals.length - 1];

        for (Float vertical : verticals) {
            compositor.drawLine(vertical, yOffset, vertical, bottom, gridPaint);
        }

        for (Float horizontal : horizontals) {
            compositor.drawLine(xOffset, horizontal, right, horizontal, gridPaint);
        }

        compositor.drawLine(xOffset, horizontals[0], verticals[0], yOffset, gridPaint);

        /*for (int row = 0; row < PUZZSIZE; row++) {
            for (int col = 0; col < PUZZSIZE; col++) {
                blitSquare(row, col, gridPaint);
            }
        }*/

        compositor.drawText(puzzle.getName(), mWidth / 2, yOffset / 2 - textPaint.getTextSize() / 2,
                textPaint);
        compositor.drawText("(difficulty: " + Integer.toString(puzzle.getDifficulty()) + "%)",
                mWidth / 2, yOffset / 2 + difficultyTextPaint.getTextSize() / 2,
                difficultyTextPaint);

        blitBack();
        blitReset();
    }

    private boolean inGrid(Coord coord) {
        return coord.x > xOffset && coord.x < xOffset + gridSize && coord.y > yOffset
                && coord.y < yOffset + gridSize;
    }

    private GridPos getPosFromCoord(Coord coord) {
        int row, col;
        row = (int)((coord.y - yOffset) / lineOffset);
        col = (int)((coord.x - xOffset) / lineOffset);
        return new GridPos(row, col);
    }

    private void reset() {
        puzzleFull = false;
        puzzleSolved = false;
        drawGrid();
        initPuzzle();
        invalidate();
    }

    private void updateGame(GridPos pos) {
        Paint paint;
        //Row or Col == 0 but NOT both
        if (!(pos.getRow() == 0 && pos.getCol() == 0) && (pos.getRow() == 0 || pos.getCol() == 0)) {
            xLine(pos);
        }
        else {
            GridPos puzzlePos = new GridPos(pos.getRow() - 1, pos.getCol() - 1);
            if (shinroPuzzle.atPos(puzzlePos) == ShinroPuzzle.EMPTY) {
                shinroPuzzle.putPoint(puzzlePos);

                numFilledRow[puzzlePos.getRow()]++;
                numFilledCol[puzzlePos.getCol()]++;

                if (highlight[puzzlePos.getRow()][puzzlePos.getCol()]) {
                    paint = pointHighlightPaint;
                }
                else {
                    paint = pointPaint;
                }

                blitPoint(puzzlePos, paint);
                blitNum(puzzlePos.getRow() + 1, 0, shinroPuzzle.getRowHeaderNum(puzzlePos.getRow()),
                        numFilledRow[puzzlePos.getRow()]);
                blitNum(0, puzzlePos.getCol() + 1, shinroPuzzle.getColHeaderNum(puzzlePos.getCol()),
                        numFilledCol[puzzlePos.getCol()]);
                //update arrows
                ArrayList<GridPos> arrows = shinroPuzzle.getPointingArrows(puzzlePos);
                if (arrows.size() > 0) {
                    for (GridPos arrow : arrows) {

                        if (highlight[arrow.getRow()][arrow.getCol()]) {
                            paint = gridHighlightPaint;
                        }
                        else {
                            paint = gridPaint;
                        }

                        blitArrow(arrow, shinroPuzzle.atPos(arrow), paint);
                    }
                }
            }
            else if (shinroPuzzle.atPos(puzzlePos) == ShinroPuzzle.POINT) {
                //update arrows first
                for (GridPos arrow : shinroPuzzle.getPointingArrows(puzzlePos)) {
                    if (shinroPuzzle.getTypeInList(ShinroPuzzle.POINT,
                            shinroPuzzle.getArrowToEdge(arrow)).size() == 1) {
                        if (highlight[arrow.getRow()][arrow.getCol()]) {
                            paint = pointHighlightPaint;
                        }
                        else {
                            paint = pointPaint;
                        }

                        blitArrow(arrow, shinroPuzzle.atPos(arrow), paint);
                    }
                }

                //now put the X
                shinroPuzzle.clearSpace(puzzlePos);
                shinroPuzzle.putX(puzzlePos);

                numFilledRow[puzzlePos.getRow()]--;
                numFilledCol[puzzlePos.getCol()]--;

                if (highlight[puzzlePos.getRow()][puzzlePos.getCol()]) {
                    paint = xHighlightPaint;
                }
                else {
                    paint = xPaint;
                }

                blitX(puzzlePos, paint);
                blitNum(puzzlePos.getRow() + 1, 0, shinroPuzzle.getRowHeaderNum(puzzlePos.getRow()),
                        numFilledRow[puzzlePos.getRow()]);
                blitNum(0, puzzlePos.getCol() + 1, shinroPuzzle.getColHeaderNum(puzzlePos.getCol()),
                        numFilledCol[puzzlePos.getCol()]);

            }
            else if (shinroPuzzle.atPos(puzzlePos) == ShinroPuzzle.X) {
                shinroPuzzle.clearSpace(puzzlePos);
                if (highlight[puzzlePos.getRow()][puzzlePos.getCol()]) {
                    paint = gridHighlightPaint;
                }
                else {
                    paint = backgroundPaint;
                }
                blitSquare(puzzlePos, paint);
            }
            else if (shinroPuzzle.isArrow(puzzlePos)) {
                ArrayList<GridPos> spaces = shinroPuzzle.getArrowToEdge(puzzlePos);
                if (highlight[puzzlePos.getRow()][puzzlePos.getCol()]) {
                    highlight[puzzlePos.getRow()][puzzlePos.getCol()] = false;
                    if (shinroPuzzle.isSatisfied(puzzlePos)) {
                        paint = gridPaint;
                    } else {
                        paint = pointPaint;
                    }
                    blitArrow(puzzlePos, shinroPuzzle.atPos(puzzlePos), paint);
                    for (GridPos space : spaces) {
                        if (!shinroPuzzle.isArrow(space)) {
                            highlight[space.getRow()][space.getCol()] = false;
                            if (shinroPuzzle.atPos(space) == ShinroPuzzle.EMPTY) {
                                paint = backgroundPaint;
                                blitSquare(space, paint);
                            } else if (shinroPuzzle.atPos(space) == ShinroPuzzle.X) {
                                paint = xPaint;
                                blitX(space, paint);
                            } else if (shinroPuzzle.atPos(space) == ShinroPuzzle.POINT) {
                                paint = pointPaint;
                                blitPoint(space, paint);
                            }
                        }
                    }
                }
                else {
                    highlight[puzzlePos.getRow()][puzzlePos.getCol()] = true;
                    if (shinroPuzzle.isSatisfied(puzzlePos)) {
                        paint = gridHighlightPaint;
                    } else {
                        paint = pointHighlightPaint;
                    }
                    blitArrow(puzzlePos, shinroPuzzle.atPos(puzzlePos), paint);
                    for (GridPos space : spaces) {
                        if (!shinroPuzzle.isArrow(space)) {
                            highlight[space.getRow()][space.getCol()] = true;
                            if (shinroPuzzle.atPos(space) == ShinroPuzzle.EMPTY) {
                                paint = gridHighlightPaint;
                                blitSquare(space, paint);
                            } else if (shinroPuzzle.atPos(space) == ShinroPuzzle.X) {
                                paint = xHighlightPaint;
                                blitX(space, paint);
                            } else if (shinroPuzzle.atPos(space) == ShinroPuzzle.POINT) {
                                paint = pointHighlightPaint;
                                blitPoint(space, paint);
                            }
                        }
                    }
                }
            }
        }

        //check for ending conditions
        if (shinroPuzzle.verifySolution()) {
            puzzleSolved = true;
            blitVictory();
        }
        else if (shinroPuzzle.puzzleGridFull()) {
            puzzleFull = true;
            blitDefeat();
        }

        invalidate();
    }

    private void xLine(GridPos pos) {
        Paint paint;
        ArrayList<GridPos> line = new ArrayList<>();
        //if the col == 0, we're getting the row - 1's empty spaces
        if (pos.getCol() == 0) {
            line = shinroPuzzle.getTypeInList(ShinroPuzzle.EMPTY,
                    shinroPuzzle.getRow(pos.getRow() - 1));
        }
        //otherwise, we're getting the col - 1's empty spaces
        else if (pos.getRow() == 0) {
            line = shinroPuzzle.getTypeInList(ShinroPuzzle.EMPTY,
                    shinroPuzzle.getCol(pos.getCol() - 1));
        }
        //if there are any empty spaces
        if (line.size() > 0) {
            for (GridPos p : line) {
                //fill them with Xs in the puzzle and grid
                shinroPuzzle.putX(p);
                if (highlight[p.getRow()][p.getCol()]) {
                    paint = xHighlightPaint;
                }
                else {
                    paint = xPaint;
                }
                blitX(p, paint);
            }
        }
        else {
            //otherwise get the Xs
            if (pos.getCol() == 0) {
                line = shinroPuzzle.getTypeInList(ShinroPuzzle.X, shinroPuzzle.getRow(pos.getRow() - 1));
            }
            else if (pos.getRow() == 0) {
                line = shinroPuzzle.getTypeInList(ShinroPuzzle.X, shinroPuzzle.getCol(pos.getCol() - 1));
            }
            if (line.size() > 0) {
                //empty the squares of their Xs in the puzzle and grid
                for (GridPos p : line) {
                    shinroPuzzle.clearSpace(p);
                    if (highlight[p.getRow()][p.getCol()]) {
                        paint = gridHighlightPaint;
                    }
                    else {
                        paint = backgroundPaint;
                    }
                    blitSquare(p, paint);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mHeight = h;
        mWidth = w;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(gameBitmap, 0, 0, blankPaint);
    }

    //TODO: Add feedback, sound
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        Coord where = new Coord();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            where.x = event.getX();
            where.y = event.getY();
            if (inGrid(where)) {
                if (puzzleSolved) {
                    //move on
                    //puzzleSolved = false;
                    if (mContext instanceof GameActivity) {
                        GameActivity activity = (GameActivity) mContext;
                        activity.incrementPuzzle(puzzle);
                    }
                }
                else if (puzzleFull) {
                    reset();
                }
                else {
                    updateGame(getPosFromCoord(where));
                }
            }
            //back button
            else if (where.x > mWidth / 4 - squareSize / 2
                    && where.x < mWidth / 4 + squareSize / 2
                    && where.y > ((mHeight - gridSize - yOffset) / 2)
                        + yOffset + gridSize - squareSize / 2
                    && where.y < ((mHeight - gridSize - yOffset) / 2)
                        + yOffset + gridSize + squareSize / 2) {
                if (mContext instanceof GameActivity) {
                    GameActivity activity = (GameActivity) mContext;
                    activity.finish();
                }
            }
            //reset button
            else if (where.x > mWidth / 4 * 3 - squareSize / 2
                    && where.x < mWidth / 4 * 3 + squareSize / 2
                    && where.y > ((mHeight - gridSize - yOffset) / 2)
                    + yOffset + gridSize - squareSize / 2
                    && where.y < ((mHeight - gridSize - yOffset) / 2)
                    + yOffset + gridSize + squareSize / 2) {
                reset();
            }
        }

        return super.onTouchEvent(event);
    }

    /*private void testUI() {
        blitX(4, 3, xPaint);
        blitX(4, 4, xHighlightPaint);

        blitSquare(3, 4, gridHighlightPaint);

        blitPoint(2, 3, pointPaint);
        blitPoint(3, 3, pointHighlightPaint);

        blitNum(0, 1, 1, 0);
        blitNum(0, 2, 2, 0);
        blitNum(0, 3, 3, 0);
        blitNum(0, 4, 4, 0);
        blitNum(0, 5, 5, 0);
        blitNum(0, 6, 6, 0);
        blitNum(0, 7, 7, 0);
        blitNum(0, 8, 8, 0);

        blitNum(8, 0, 8, 9);
        blitNum(7, 0, 8, 7);
        blitNum(6, 0, 8, 6);
        blitNum(5, 0, 8, 5);
        blitNum(4, 0, 8, 4);
        blitNum(3, 0, 8, 3);
        blitNum(2, 0, 8, 2);
        blitNum(1, 0, 8, 1);

        blitArrow(0, 0, ShinroPuzzle.N, pointPaint);
        blitArrow(1, 0, ShinroPuzzle.S, pointPaint);
        blitArrow(2, 0, ShinroPuzzle.E, pointPaint);
        blitArrow(3, 0, ShinroPuzzle.W, pointPaint);
        blitArrow(4, 0, ShinroPuzzle.NE, pointPaint);
        blitArrow(5, 0, ShinroPuzzle.NW, pointPaint);
        blitArrow(6, 0, ShinroPuzzle.SW, pointPaint);
        blitArrow(7, 0, ShinroPuzzle.SE, pointPaint);

        blitArrow(0, 1, ShinroPuzzle.N, pointHighlightPaint);
        blitArrow(1, 1, ShinroPuzzle.S, pointHighlightPaint);
        blitArrow(2, 1, ShinroPuzzle.E, pointHighlightPaint);
        blitArrow(3, 1, ShinroPuzzle.W, pointHighlightPaint);
        blitArrow(4, 1, ShinroPuzzle.NE, pointHighlightPaint);
        blitArrow(5, 1, ShinroPuzzle.NW, pointHighlightPaint);
        blitArrow(6, 1, ShinroPuzzle.SW, pointHighlightPaint);
        blitArrow(7, 1, ShinroPuzzle.SE, pointHighlightPaint);

        blitArrow(0, 0, ShinroPuzzle.N, pointPaint);
        blitArrow(1, 0, ShinroPuzzle.S, pointPaint);
        blitArrow(2, 0, ShinroPuzzle.E, pointPaint);
        blitArrow(3, 0, ShinroPuzzle.W, pointPaint);
        blitArrow(4, 0, ShinroPuzzle.NE, pointPaint);
        blitArrow(5, 0, ShinroPuzzle.NW, pointPaint);
        blitArrow(6, 0, ShinroPuzzle.SW, pointPaint);
        blitArrow(7, 0, ShinroPuzzle.SE, pointPaint);

        blitArrow(0, 1, ShinroPuzzle.N, pointHighlightPaint);
        blitArrow(1, 1, ShinroPuzzle.S, pointHighlightPaint);
        blitArrow(2, 1, ShinroPuzzle.E, pointHighlightPaint);
        blitArrow(3, 1, ShinroPuzzle.W, pointHighlightPaint);
        blitArrow(4, 1, ShinroPuzzle.NE, pointHighlightPaint);
        blitArrow(5, 1, ShinroPuzzle.NW, pointHighlightPaint);
        blitArrow(6, 1, ShinroPuzzle.SW, pointHighlightPaint);
        blitArrow(7, 1, ShinroPuzzle.SE, pointHighlightPaint);

        blitArrow(0, 7, ShinroPuzzle.SE, gridPaint);
        blitArrow(1, 7, ShinroPuzzle.SW, gridPaint);
        blitArrow(2, 7, ShinroPuzzle.NW, gridPaint);
        blitArrow(3, 7, ShinroPuzzle.NE, gridPaint);
        blitArrow(4, 7, ShinroPuzzle.W, gridPaint);
        blitArrow(5, 7, ShinroPuzzle.E, gridPaint);
        blitArrow(6, 7, ShinroPuzzle.S, gridPaint);
        blitArrow(7, 7, ShinroPuzzle.N, gridPaint);

        blitArrow(0, 6, ShinroPuzzle.SE, gridHighlightPaint);
        blitArrow(1, 6, ShinroPuzzle.SW, gridHighlightPaint);
        blitArrow(2, 6, ShinroPuzzle.NW, gridHighlightPaint);
        blitArrow(3, 6, ShinroPuzzle.NE, gridHighlightPaint);
        blitArrow(4, 6, ShinroPuzzle.W, gridHighlightPaint);
        blitArrow(5, 6, ShinroPuzzle.E, gridHighlightPaint);
        blitArrow(6, 6, ShinroPuzzle.S, gridHighlightPaint);
        blitArrow(7, 6, ShinroPuzzle.N, gridHighlightPaint);
    }*/
}