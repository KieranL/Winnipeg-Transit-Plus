package winnipegbusbackend;

public enum TimeStatuses {
    Ok("Ok"),
    Late("Late"),
    Early("Erly");

    public String status;

    TimeStatuses(String status) {
        this.status = status;
    }
}
