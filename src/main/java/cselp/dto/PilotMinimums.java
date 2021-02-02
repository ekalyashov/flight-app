package cselp.dto;


import java.io.Serializable;

public class PilotMinimums implements Serializable {
    private Double land_vis_h;
    private Double land_vis_v;
    private Double take_off_vis_h;

    public Double getLand_vis_h() {
        return land_vis_h;
    }

    public void setLand_vis_h(Double land_vis_h) {
        this.land_vis_h = land_vis_h;
    }

    public Double getLand_vis_v() {
        return land_vis_v;
    }

    public void setLand_vis_v(Double land_vis_v) {
        this.land_vis_v = land_vis_v;
    }

    public Double getTake_off_vis_h() {
        return take_off_vis_h;
    }

    public void setTake_off_vis_h(Double take_off_vis_h) {
        this.take_off_vis_h = take_off_vis_h;
    }
}
