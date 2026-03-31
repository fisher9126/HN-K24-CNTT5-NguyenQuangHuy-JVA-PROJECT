package booking.entity;

public class EquipmentBooking {
    private int id;
    private int bookingId;
    private int equipmentId;
    private int quantity;

    public EquipmentBooking() {
    }

    public EquipmentBooking(int id, int bookingId, int equipmentId, int quantity) {
        this.id = id;
        this.bookingId = bookingId;
        this.equipmentId = equipmentId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "EquipmentBooking{" +
                "id=" + id +
                ", bookingId=" + bookingId +
                ", equipmentId=" + equipmentId +
                ", quantity=" + quantity +
                '}';
    }
}
