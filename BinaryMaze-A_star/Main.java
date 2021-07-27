import java.io.*;
import java.util.ArrayList;

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
            graph.A_Star();

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

    public void A_Star()
    {
        ArrayList<Node> choices = new ArrayList<>();
        choices.add(Snake);

        do
        {
            Node current = choices.get(findMinIndex(choices));

            if (current == Apple)
                break;

            AddNeighbors(current, choices);
            current.Processed = true;
            choices.remove(current);
        }
        while (choices.isEmpty() == false);

        String path = GetPath(Apple);
        PrintPath(path);
    }

    public int h(Node node)
    {
        return Math.abs(Apple.Row - node.Row) % Row + Math.abs(Apple.Col - node.Col) % Col;
    }

    public int f(Node node)
    {
        return h(node) + node.cost;
    }

    public String GetPath(Node current)
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
        {
            System.out.print("\033[0;33m" + "There is no path form 'S' to 'A'");
            return;
        }

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

    public void AddNeighbors(Node current, ArrayList<Node> choices)
    {
        Node up = UpNeighbor(current);
        Node down = DownNeighbor(current);
        Node left = LeftNeighbor(current);
        Node right = RightNeighbor(current);

        if (up.State != '1' && up.Processed == false)
        {
            if (choices.contains(up))
            {
                if (up.cost + 1 < current.cost)
                {
                    up.Parent = Parent.DOWN;
                    up.cost = current.cost + 1;
                }

            } else
            {
                up.cost = current.cost + 1;
                up.Parent = Parent.DOWN;
                choices.add(up);
            }
        }

        if (down.State != '1' && down.Processed == false)
        {
            if (choices.contains(down))
            {
                if (down.cost + 1 < current.cost)
                {
                    down.Parent = Parent.UP;
                    down.cost = current.cost + 1;
                }
            } else
            {
                down.cost = current.cost + 1;
                down.Parent = Parent.UP;
                choices.add(down);
            }
        }

        if (left.State != '1' && left.Processed == false)
        {
            if (choices.contains(left))
            {
                left.Parent = Parent.RIGHT;
                left.cost = current.cost + 1;
            } else
            {
                left.cost = current.cost + 1;
                left.Parent = Parent.RIGHT;
                choices.add(left);
            }
        }

        if (right.State != '1' && right.Processed == false)
        {
            if (choices.contains(right))
            {
                right.Parent = Parent.LEFT;
                right.cost = current.cost + 1;
            } else
            {
                right.cost = current.cost + 1;
                right.Parent = Parent.LEFT;
                choices.add(right);
            }
        }
    }

    public int findMinIndex(ArrayList<Node> arrayList)
    {
        int minIndex = 0;

        for (int i = 0; i < arrayList.size(); i++)
        {
            int h_i = f(arrayList.get(i));
            int h_min = f(arrayList.get(minIndex));

            if (h_i < h_min && arrayList.get(i).State != '1' && arrayList.get(i).Processed == false)
            {
                minIndex = i;
            }
        }
        return minIndex;
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
    boolean Processed;
    int cost;

    public Node(int row, int col, char ch)
    {
        Row = row;
        Col = col;
        State = ch;
        Processed = false;
        cost = 0;
    }
}

enum Parent
{
    UP, DOWN, LEFT, RIGHT;
}



