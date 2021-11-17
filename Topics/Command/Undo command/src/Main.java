
interface Movable {
    int getX();

    int getY();

    void setX(int newX);

    void setY(int newY);
}

interface Storable {
    int getInventoryLength();

    String getInventoryItem(int index);

    void setInventoryItem(int index, String item);
}

interface Command {
    void execute();

    void undo();
}

class CommandMove implements Command {
    Movable entity;
    int xMovement;
    int yMovement;

    @Override
    public void execute() {
        entity.setX(entity.getX() + xMovement);
        entity.setY(entity.getY() + yMovement);
    }

    @Override
    public void undo() {
        entity.setX(entity.getX() - xMovement);
        entity.setY(entity.getY() - yMovement);
    }
}

class CommandPutItem implements Command {
    Storable entity;
    String item;

    private int getEmptyIndex() {
        int length = entity.getInventoryLength();
        for (int i = 0; i < length; i++) {
            if (entity.getInventoryItem(i) == null) {
                return i;
            }
        }
        return length;
    }

    @Override
    public void execute() {
        int index = getEmptyIndex();
        if (index < entity.getInventoryLength()) {
            entity.setInventoryItem(index, item);
        }
    }

    @Override
    public void undo() {
        int index = getEmptyIndex();
        if (index != 0) {
            entity.setInventoryItem(index - 1, null);
        }
    }
}