import java.util.*;

public class Main {
    public class Node {
        private int id;
        private int fuelCost;
        private LinkedList<Node> pathFromRoot = new LinkedList<>();
        private long distanceFromRoot;

        public Node(int id, int distance, int fuelCost, Node parent) {
            this.id = id;
            this.fuelCost = fuelCost;
            if (parent == null) {
                this.pathFromRoot.add(this);
                this.distanceFromRoot = 0;
            } else {
                this.pathFromRoot = new LinkedList<>(parent.pathFromRoot);
                this.pathFromRoot.add(this);
                this.distanceFromRoot = parent.distanceFromRoot + distance;
            }
        }

        // Finds the shortest distance from this node to another node.
        public long distanceToNode(Node node) {
            LinkedList<Node> currNodePath = new LinkedList<>(this.pathFromRoot);
            LinkedList<Node> otherNodePath = new LinkedList<>(node.pathFromRoot);

            // Compare the two node's pathFromRoot to find LCA.
            Node lowestCommonAncestor;
            do {
                lowestCommonAncestor = currNodePath.poll();
                otherNodePath.poll();
            } while (!currNodePath.isEmpty() && !otherNodePath.isEmpty() &&
                    currNodePath.peek() == otherNodePath.peek());

            return this.distanceFromRoot + node.distanceFromRoot
                    - 2 * lowestCommonAncestor.distanceFromRoot;
        }

        @Override
        public String toString() {
            return String.valueOf(this.id);
        }
    }
    private int n;

    private int[] cost;

    private int[] parent;

    private int[] distance;

    public Main() {
        Scanner scan = new Scanner(System.in);
        this.n = scan.nextInt();

        this.cost = new int[this.n];
        for (int i = 0; i < this.n; i++) {
            this.cost[i] = scan.nextInt();
        }

        this.parent = new int[this.n - 1];
        this.distance = new int[this.n - 1];
        for(int i = 0; i < this.n - 1; i++) {
            this.parent[i] = scan.nextInt();
            this.distance[i] = scan.nextInt();
        }
    }

    public long findLowestCost() {
        List<Node> nodes = new ArrayList<>();
        List<Node> sortedByCost = new ArrayList<>();

        Node root = new Node(0, 0, this.cost[0], null);
        nodes.add(root);
        sortedByCost.add(root);

        for (int i = 0; i < this.n - 2; i++) {
            Node node = new Node(i + 1, this.distance[i], this.cost[i + 1], nodes.get(this.parent[i] - 1));
            nodes.add(i + 1, node);
            if (node.fuelCost < root.fuelCost) {
                sortedByCost.add(node);
            }
        }

        Node target = new Node(this.n - 1, this.distance[this.n - 2],
                             this.cost[this.n - 1], nodes.get(this.parent[this.n - 2] - 1));

        // Sort the nodes by decreasing order of fuel costs.
        sortedByCost.sort(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return n2.fuelCost - n1.fuelCost;
            }
        });

        // DP array storing smallest cost to travel from root to each node.
        int len = sortedByCost.size();
        long[] dp = new long[len];
        for (int i = 0; i < len; i++) {
            Node curr = sortedByCost.get(i);
            dp[i] = root.fuelCost * curr.distanceFromRoot;
            for (int j = 0; j < i; j++) {
                long distanceBetween = curr.distanceToNode(sortedByCost.get(j));
                long extraCost = sortedByCost.get(j).fuelCost * distanceBetween;
                dp[i] = Math.min(dp[i], dp[j] + extraCost);
            }
        }

        // Final iteration to find smallest cost to travel from
        // root to destination by going through each node.
        long result = Long.MAX_VALUE;
        for (int i = 0; i < len; i++) {
            long distanceBetween = sortedByCost.get(i).distanceToNode(target);
            long extraCost = sortedByCost.get(i).fuelCost * distanceBetween;
            result = Math.min(result, dp[i] + extraCost);
        }

        return result;
    }

    public static void main(String[] args) {
        Main main = new Main();

        System.out.println(main.findLowestCost());
    }
}