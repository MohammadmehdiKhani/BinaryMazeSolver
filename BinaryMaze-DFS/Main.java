import java.io.*;
import java.util.ArrayList;
import java.io.IOException;

public class Main
{

    public static void main(String[] args) throws IOException
    {
        try
        {
            ClassLoader classLoader = Main.class.getClassLoader();
            File file = new File(classLoader.getResource("Matrix.TXT").getFile());
            Helper helper = new Helper();
            char[][] matrix = helper.ReadFromFile(file);


            Graph graph = new Graph(matrix, matrix.length, matrix[0].length);
            boolean[][] allFalse = new boolean[matrix.length][matrix[0].length];
            graph.DFS(allFalse, graph.Snake, "");
            String path = graph.GetTheSmallestPath();
            System.out.println(path);
        }catch (Exception e)
        {
            System.out.println("\033[0;31m"
                    + "Move the Matrix.TXT in 'ProjectFolder/src/' Directory" + "\033[0m");
        }
    }
}

class Graph
{
    Node[][] Nodes;
    int Row;
    int Col;
    Node Snake;
    Node Apple;
    ArrayList<String> AllPathsDFS;

    public Graph(char[][] matrix, int row, int col)
    {
        Nodes = new Node[row][col];
        for (int i = 0; i < row; i++)
        {
            for (int j = 0; j < col; j++)
            {
                char ch = matrix[i][j];
                Node node = new Node(i, j, ch);
                Nodes[i][j] = node;

                if (ch == 'A')
                    Apple = Nodes[i][j];

                if (ch == 'S')
                    Snake = Nodes[i][j];
            }
        }
        Row = row;
        Col = col;

        AllPathsDFS = new ArrayList<>();
    }

    public void DFS(boolean[][] visited, Node current, String pathFromS)
    {
        visited[current.Row][current.Col] = true;

        if (current == Apple)
            AllPathsDFS.add(getPath(Apple));

        Node up = UpNeighbor(current);
        Node down = DownNeighbor(current);
        Node left = LeftNeighbor(current);
        Node right = RightNeighbor(current);

        //no one of neighbors processed
        if (up.State != '1' && visited[up.Row][up.Col] == false)
        {
            up.Parent = Parent.DOWN;
            pathFromS += "Up ";
            boolean[][] visited1 = new boolean[Row][Col];

            for (int i = 0; i < Row; i++)
                for (int j = 0; j < Col; j++)
                    visited1[i][j] = visited[i][j];

            DFS(visited1, up, pathFromS);
        }

        //up is processed
        if (down.State != '1' && visited[down.Row][down.Col] == false)
        {
            down.Parent = Parent.UP;
            pathFromS += "Down ";
            boolean[][] visited1 = new boolean[Row][Col];

            for (int i = 0; i < Row; i++)
                for (int j = 0; j < Col; j++)
                    visited1[i][j] = visited[i][j];

            DFS(visited1, down, pathFromS);
        }

        //up - down of is processed
        if (left.State != '1' && visited[left.Row][left.Col] == false)
        {
            left.Parent = Parent.RIGHT;
            pathFromS += "Left ";
            boolean[][] visited1 = new boolean[Row][Col];

            for (int i = 0; i < Row; i++)
                for (int j = 0; j < Col; j++)
                    visited1[i][j] = visited[i][j];

            DFS(visited1, left, pathFromS);
        }

        //up - down - left of is processed
        if (right.State != '1' && visited[right.Row][right.Col] == false)
        {
            right.Parent = Parent.LEFT;
            pathFromS += "Right ";
            boolean[][] visited1 = new boolean[Row][Col];

            for (int i = 0; i < Row; i++)
                for (int j = 0; j < Col; j++)
                    visited1[i][j] = visited[i][j];

            DFS(visited1, right, pathFromS);
        }
    }

    public String getPath(Node current)
    {
        String path = "";
        Node next = null;

        while (current.Parent != null)
        {
            int i = current.Row;
            int j = current.Col;

            if (current.Parent == Parent.UP)
            {
                path += "Down ";

                if (i == 0)
                    next = Nodes[Row - 1][j];
                else
                    next = Nodes[i - 1][j];
            }

            if (current.Parent == Parent.DOWN)
            {
                path += "Up ";

                if (i == Row - 1)
                    next = Nodes[0][j];
                else
                    next = Nodes[i + 1][j];

            }

            if (current.Parent == Parent.LEFT)
            {
                path += "Right ";

                if (j == 0)
                    next = Nodes[i][Col - 1];
                else
                    next = Nodes[i][j - 1];
            }

            if (current.Parent == Parent.RIGHT)
            {
                path += "Left ";

                if (j == Col - 1)
                    next = Nodes[i][0];
                else
                    next = Nodes[i][j + 1];
            }

            current = next;
        }
        return path;
    }

    public void PrintPath(String path)
    {
        String[] steps = path.split(" ");

        for (int i = steps.length - 1; i >= 0; i--)
        {
            System.out.print(steps[i]);
            if (i != 0)
                System.out.print(" ");
        }
    }

    public Node UpNeighbor(Node current)
    {
        //up border
        if (current.Row == 0)
            return Nodes[Row - 1][current.Col];

        else
            return Nodes[current.Row - 1][current.Col];
    }

    public Node DownNeighbor(Node current)
    {
        //down border
        if (current.Row == Row - 1)
            return Nodes[0][current.Col];

        else
            return Nodes[current.Row + 1][current.Col];
    }

    public Node LeftNeighbor(Node current)
    {
        //left border
        if (current.Col == 0)
            return Nodes[current.Row][Col - 1];

        else
            return Nodes[current.Row][current.Col - 1];
    }

    public Node RightNeighbor(Node current)
    {
        //right border
        if (current.Col == Col - 1)
            return Nodes[current.Row][0];

        else
            return Nodes[current.Row][current.Col + 1];
    }

    public String GetTheSmallestPath()
    {
        if (AllPathsDFS.size() == 0)
            return "\033[0;33m" + "There is no path form 'S' to 'A'";

        int minIndex = AllPathsDFS.size() - 1;

        String shortestPath;
        for (int i = AllPathsDFS.size() - 1; i >= 0; i--)
        {
            if (AllPathsDFS.get(i).split(" ").length < AllPathsDFS.get(AllPathsDFS.size() - 1).split(" ").length)
                minIndex = i;
        }

        String[] steps = AllPathsDFS.get(minIndex).split(" ");
        String finalPath = "";

        for (int i = steps.length - 1; i >= 0; i--)
        {
            finalPath += steps[i] + " ";
        }

        return finalPath;
    }
}

class Helper
{
    public char[][] ReadFromFile(File file) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(file));

        int row = 0;
        int col = 0;
        String st;

        st = br.readLine();
        row++;
        col = st.split(" ").length;

        while ((st = br.readLine()) != null)
            row++;

        char[][] matrix = new char[row][col];

        BufferedReader br1 = new BufferedReader(new FileReader(file));

        for (int i = 0; i < row; i++)
        {
            String line = br1.readLine();

            for (int j = 0; j < col; j++)
            {
                String[] chars = line.split(" ");
                matrix[i][j] = chars[j].charAt(0);
            }
        }
        return matrix;
    }
}

class Node
{
    char State; //0 means ok, 1 means blocked, S is source, A is apple
    int Row;
    int Col;
    Parent Parent;
    int Depth;

    Node(int row, int col, char ch)
    {
        Row = row;
        Col = col;
        State = ch;
        Depth = 0;
    }
}

enum Parent
{
    UP, DOWN, LEFT, RIGHT;
}



