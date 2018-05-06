package it.polimi.se2018.model;

//The class that describes the Switch action
public class Switch implements Action {
    private DieCoord coord1;
    private DieCoord coord2;

    public Switch(DieCoord coord1, DieCoord coord2) {
        this.coord1 = coord1;
        this.coord2 = coord2;
    }

    public PlacementError check(){
        PlacementError err = new PlacementError();

        //Check errors for d2->coord1 (d1 already in coord2)
        coord1.saveState();
        coord2.saveState();
        Die d1 = coord1.get();
        Die d2 = coord2.get();
        coord1.set(d2);
        err = PlacementError.union(err, coord2.isAllowed(d1));
        coord1.restoreState();
        coord2.restoreState();

        //Check errors for d1->coord2 (d2 already in coord1)
        coord1.saveState();
        coord2.saveState();
        d1 = coord1.get();
        d2 = coord2.get();
        coord2.set(d1);
        err = PlacementError.union(err, coord1.isAllowed(d2));

        coord1.restoreState();
        coord2.restoreState();

        return err;
    }

    public void perform(){
        Die tmp = coord1.get();
        coord1.set(coord2.get());
        coord2.set(tmp);
    }
}
