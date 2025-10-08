using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;

public class Logic : MonoBehaviour
{
    // points:
    private Vector3 leftPoint;
    private Vector3 rightPoint;

    private Vector3 upLeftPoint;
    private Vector3 upRightPoint;

    private Vector3 downLeftPoint;
    private Vector3 downRightPoint;

    // general:
    private movingState state = movingState.NONE;
    private int pencilsQuantity;
    private float distanceBetweenPencils;

    private float time = 0;
    private const float timeToMove = 0.3f;

    private const int maxPencilsQuantity = 12;
    private const float playGroundSizeX = 15f;

    private const float pencilsPositionY = -4f;
    private const float minDistanceBetweenPencils = 0.2f;
    private const float pencilSpriteSizeX = (playGroundSizeX - minDistanceBetweenPencils * (maxPencilsQuantity - 1)) / maxPencilsQuantity;

    // pencils:
    public GameObject[] pencilsObjects;
    private GameObject[] pencils;
    private int[] pencilsIndex;

    private const float pencilOriginalSpriteSizeX = 1.06f;
    private const float pencilScale = pencilSpriteSizeX / pencilOriginalSpriteSizeX;

    // circles:
    public GameObject[] circlesObjects;
    private GameObject[] circles;
    private int[] circlesIndex;

    private const float circleOriginalSpriteSizeX = 3.01f;
    private const float circleScale = pencilSpriteSizeX / circleOriginalSpriteSizeX;

    void Start()
    {
        pencilsQuantity = 4;
        spawnPencils();
        spawnPoints();

        //Debug.Log(pencils[0].GetComponent<SpriteRenderer>().bounds.size.x);
        //Debug.Log(circles[0].GetComponent<SpriteRenderer>().bounds.size.x);
    }

    void Update()
    {
        switch (state)
        {
            case movingState.NONE:
                if (Input.GetKey(KeyCode.LeftArrow)) state = movingState.LEFT;
                else if (Input.GetKey(KeyCode.RightArrow)) state = movingState.RIGHT;
                else if (Input.GetKey(KeyCode.Space)) state = movingState.SWITCH;
                break;

            case movingState.RIGHT:
                time += Time.deltaTime;
                if (time > timeToMove)
                {
                    state = movingState.NONE;
                    time = 0;
                    pencils[0].transform.position = rightPoint;
                    pencils[0].transform.Translate(new Vector3(0, 0, 1));
                    movePencilRight();
                }
                else
                {
                    pencils[0].transform.position = getPointBezierRight(time / timeToMove);
                    for (int i = 1; i < pencilsQuantity; ++i)
                    {
                        pencils[i].transform.Translate(
                            new Vector3(-(pencilSpriteSizeX + distanceBetweenPencils) * Time.deltaTime / timeToMove, 0, 0));
                    }
                }
                break;

            case movingState.LEFT:
                time += Time.deltaTime;
                if (time > timeToMove)
                {
                    state = movingState.NONE;
                    time = 0;
                    pencils[pencilsQuantity - 1].transform.position = leftPoint;
                    pencils[pencilsQuantity - 1].transform.Translate(new Vector3(0, 0, 1));
                    movePencilLeft();
                }
                else
                {
                    pencils[pencilsQuantity - 1].transform.position = getPointBezierLeft(time / timeToMove);
                    for (int i = 0; i < pencilsQuantity - 1; ++i)
                    {
                        pencils[i].transform.Translate(
                            new Vector3((pencilSpriteSizeX + distanceBetweenPencils) * Time.deltaTime / timeToMove, 0, 0));
                    }
                }
                break;

            case movingState.SWITCH:
                time += Time.deltaTime;
                if (time > timeToMove)
                {
                    state = movingState.NONE;
                    time = 0;

                    pencils[0].transform.position = rightPoint;
                    pencils[0].transform.Translate(new Vector3(0, 0, 1));
                    pencils[pencilsQuantity - 1].transform.position = leftPoint;
                    pencils[pencilsQuantity - 1].transform.Translate(new Vector3(0, 0, 1));

                    switchPenils();
                }
                else
                {
                    pencils[pencilsQuantity - 1].transform.position = getPointBezierLeft(time / timeToMove);
                    pencils[0].transform.position = getPointBezierRight(time / timeToMove);
                }
                break;
        }

        if (Input.GetKeyUp(KeyCode.Escape))
        {
            deletePencils();
            pencilsQuantity++;
            if (pencilsQuantity > maxPencilsQuantity) pencilsQuantity = 4;
            spawnPencils();
        }

        /*pencils[0].transform.Translate(new Vector3(0, 0.5f, 0) * Time.deltaTime);
        pencils[1].transform.Rotate(new Vector3(0.5f, 5f, 0) * Time.deltaTime);
        pencils[2].transform.localScale += new Vector3(0, 0.1f, 0) * Time.deltaTime;*/
    }

    public bool equals(int[] a, int[] b)
    {
        if (a.Length != b.Length) return false;
        for (int i = 0; i < a.Length; ++i)
        {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    public void randomFilling(int[] array)
    {
        for (int i = 0; i < array.Length; ++i)
        {
            int nextPencilIndex = Random.Range(0, array.Length);
            bool flag = true;
            for (int j = 0; j < i; j++)
            {
                if (nextPencilIndex == array[j]) flag = false;
            }
            if (flag) array[i] = nextPencilIndex;
            else i--;
        }
    }

    public Vector3 getPointBezierRight(float t)
    {
        float oneMinusT = 1 - t;
        return
            leftPoint * (oneMinusT * oneMinusT * oneMinusT) +
            upLeftPoint * (3 * oneMinusT * oneMinusT * t) +
            upRightPoint * (3 * oneMinusT * t * t) +
            rightPoint * (t * t * t);
    }

    public Vector3 getPointBezierLeft(float t)
    {
        float oneMinusT = 1 - t;
        return
            rightPoint * (oneMinusT * oneMinusT * oneMinusT) +
            downRightPoint * (3 * oneMinusT * oneMinusT * t) +
            downLeftPoint * (3 * oneMinusT * t * t) +
            leftPoint * (t * t * t);
    }

    public void spawnPencils()
    {
        distanceBetweenPencils = (playGroundSizeX - pencilsQuantity * pencilSpriteSizeX) / (pencilsQuantity - 1);

        pencilsIndex = new int[pencilsQuantity];
        circlesIndex = new int[pencilsQuantity];

        pencils = new GameObject[pencilsQuantity];
        circles = new GameObject[pencilsQuantity];

        randomFilling(circlesIndex);
        while (true)
        {
            randomFilling(pencilsIndex);
            if (!equals(pencilsIndex, circlesIndex)) break;
        }

        float startPosition = (pencilSpriteSizeX - playGroundSizeX) / 2;
        float distanceBetweenCenters = pencilSpriteSizeX + distanceBetweenPencils;

        for (int i = 0; i < pencilsQuantity; i++)
        {
            pencils[i] = Instantiate(pencilsObjects[pencilsIndex[i]],
                new Vector3(startPosition + i * distanceBetweenCenters, pencilsPositionY, 0),
                Quaternion.Euler(0, 0, 0));
            pencils[i].transform.localScale = new Vector3(pencilScale, pencilScale, 1);

            circles[i] = Instantiate(circlesObjects[circlesIndex[i]],
                new Vector3(startPosition + i * distanceBetweenCenters, 4f, 0),
                Quaternion.Euler(0, 0, 0));
            circles[i].transform.localScale = new Vector3(circleScale, circleScale, 1);
        }
    }

    public void deletePencils()
    {
        for (int i = 0; i < pencilsQuantity; i++)
        {
            Destroy(pencils[i]);
            Destroy(circles[i]);
        }
    }

    public void spawnPoints()
    {
        float leftPointPositionX = (pencilSpriteSizeX - playGroundSizeX) / 2;

        float distanceBetweenPointsY = 6f;
        float distanceBetweenPointsX = 4f;

        leftPoint = new Vector3(leftPointPositionX, pencilsPositionY, -1f);
        rightPoint = new Vector3(-leftPointPositionX, pencilsPositionY, -1f);

        upLeftPoint = new Vector3(leftPointPositionX + distanceBetweenPointsX,
            pencilsPositionY + distanceBetweenPointsY, -1f);
        upRightPoint = new Vector3(-leftPointPositionX - distanceBetweenPointsX,
            pencilsPositionY + distanceBetweenPointsY, -1f);

        downLeftPoint = new Vector3(leftPointPositionX + distanceBetweenPointsX,
            pencilsPositionY - distanceBetweenPointsY, -1f);
        downRightPoint = new Vector3(-leftPointPositionX - distanceBetweenPointsX,
            pencilsPositionY - distanceBetweenPointsY, -1f);
    }

    public void movePencilRight()
    {
        int buffer = pencilsIndex[0];
        GameObject bufferObject = pencils[0];
        for (int i = 1; i < pencilsIndex.Length; ++i)
        {
            pencilsIndex[i - 1] = pencilsIndex[i];
            pencils[i - 1] = pencils[i];
        }
        pencilsIndex[pencilsIndex.Length - 1] = buffer;
        pencils[pencilsIndex.Length - 1] = bufferObject;
    }

    public void movePencilLeft()
    {
        int buffer = pencilsIndex[pencilsIndex.Length - 1];
        GameObject bufferObject = pencils[pencilsIndex.Length - 1];
        for (int i = pencilsIndex.Length - 1; i > 0; --i)
        {
            pencilsIndex[i] = pencilsIndex[i - 1];
            pencils[i] = pencils[i - 1];
        }
        pencilsIndex[0] = buffer;
        pencils[0] = bufferObject;
    }

    public void switchPenils()
    {
        int buffer = pencilsIndex[0];
        pencilsIndex[0] = pencilsIndex[pencilsIndex.Length - 1];
        pencilsIndex[pencilsIndex.Length - 1] = buffer;

        GameObject bufferObject = pencils[0];
        pencils[0] = pencils[pencilsIndex.Length - 1];
        pencils[pencilsIndex.Length - 1] = bufferObject;
    }

    enum movingState
    {
        NONE,
        RIGHT,
        LEFT,
        SWITCH
    }
}
