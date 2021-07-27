
import javax.swing.plaf.ColorUIResource;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.io.IOException;

public class Main
{

    public static void main(String[] args) throws IOException
    {
        try
        {
            ClassLoader classLoader = Main.class.getClassLoader();
            File file = new File(classLoader.getResource("Matrix.TXT").getFile());
            Reader reader = new Reader();
            char[][] matrix = reader.ReadFromFile(file);

            Graph graph = new Graph(matrix, matrix.length, matrix[0].length);
            graph.BFS();
        } catch (Exception e)
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
    }

    public void BFS()
    {
        Queue<Node> queue = new LinkedList<>();
        queue.add(Snake);
        Snake.Visited = true;

        while (queue.isEmpty() == false)
        {
            Node current = queue.poll();
            if (queue.contains(Apple))
                break;

            Helper.PleaseHelp.AddNeighborsBFS(queue, current, Row - 1, Col - 1, Nodes);
        }

        String path = getPath(Apple);
        PrintPath(path);
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
        if (path.equals(""))
            System.out.print("\033[0;33m" + "There is no path form 'S' to 'A'");

        String[] steps = path.split(" ");

        for (int i = steps.length - 1; i >= 0; i--)
        {
            System.out.print(steps[i] + " ");
        }
    }
}

class Reader
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

class Helper
{
    static class PleaseHelp
    {
        public static void AddNeighborsBFS(Queue<Node> queue, Node current, int row, int col, Node[][] Nodes)
        {
            Node up, down, left, right;
            up = null;
            down = null;
            left = null;
            right = null;

            //center
            if (current.Row > 0 & current.Row < row & current.Col > 0 & current.Col < col)
            {
                up = Nodes[current.Row - 1][current.Col];
                down = Nodes[current.Row + 1][current.Col];
                left = Nodes[current.Row][current.Col - 1];
                right = Nodes[current.Row][current.Col + 1];
            }

            //up border
            if (current.Row == 0 & current.Col != 0 & current.Col != col)
            {
                up = Nodes[row][current.Col];
                down = Nodes[current.Row + 1][current.Col];
                left = Nodes[current.Row][current.Col - 1];
                right = Nodes[current.Row][current.Col + 1];
            }

            //down border
            if (current.Row == row & current.Col != 0 & current.Col != col)
            {
                up = Nodes[current.Row - 1][current.Col];
                down = Nodes[0][current.Col];
                left = Nodes[current.Row][current.Col - 1];
                right = Nodes[current.Row][current.Col + 1];
            }

            //left border
            if (current.Col == 0 & current.Row != 0 & current.Row != row)
            {
                up = Nodes[current.Row - 1][current.Col];
                down = Nodes[current.Row + 1][current.Col];
                left = Nodes[current.Row][col];
                right = Nodes[current.Row][current.Col + 1];
            }

            //right border
            if (current.Col == col & current.Row != 0 & current.Row != row)
            {
                up = Nodes[current.Row - 1][current.Col];
                down = Nodes[current.Row + 1][current.Col];
                left = Nodes[current.Row][current.Col - 1];
                right = Nodes[current.Row][0];
            }

            //up-left corner
            if (current.Row == 0 & current.Col == 0)
            {
                up = Nodes[row][current.Col];
                down = Nodes[current.Row + 1][current.Col];
                left = Nodes[current.Row][col];
                right = Nodes[current.Row][current.Col + 1];
            }

            //up-right corner
            if (current.Row == 0 & current.Col == col)
            {
                up = Nodes[row][current.Col];
                down = Nodes[current.Row + 1][current.Col];
                left = Nodes[current.Row][current.Col - 1];
                right = Nodes[current.Row][0];
            }

            //down-left corner
            if (current.Row == row & current.Col == 0)
            {
                up = Nodes[current.Row - 1][current.Col];
                down = Nodes[0][current.Col];
                left = Nodes[current.Row][col];
                right = Nodes[current.Row][current.Col + 1];
            }

            //down-right corner
            if (current.Row == row & current.Col == col)
            {
                up = Nodes[current.Row - 1][current.Col];
                down = Nodes[0][current.Col];
                left = Nodes[current.Row][current.Col - 1];
                right = Nodes[current.Row][0];
            }

            if (up.Visited == false & up.State != '1')
            {
                up.Visited = true;
                up.Parent = Parent.DOWN;
                queue.add(up);
            }
            if (down.Visited == false & down.State != '1')
            {
                down.Visited = true;
                down.Parent = Parent.UP;
                queue.add(down);
            }
            if (left.Visited == false & left.State != '1')
            {
                left.Visited = true;
                left.Parent = Parent.RIGHT;
                queue.add(left);
            }
            if (right.Visited == false & right.State != '1')
            {
                right.Visited = true;
                right.Parent = Parent.LEFT;
                queue.add(right);
            }
        }
    }
}

class Node
{
    char State; //0 means ok, 1 means blocked, S is source, A is apple
    int Row;
    int Col;
    Parent Parent;
    boolean Visited;

    public Node(int row, int col, char ch)
    {
        Row = row;
        Col = col;
        State = ch;
        Visited = false;
    }
}

enum Parent
{
    UP, DOWN, LEFT, RIGHT;
}


