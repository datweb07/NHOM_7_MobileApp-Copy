package com.rescuefarm.ui.admin;

import com.rescuefarm.data.repository.AdminRepository.Section;
import com.rescuefarm.data.repository.admin.AdminDashboardSnapshot;
import com.rescuefarm.data.repository.admin.AdminListItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AdminScreenState {
    public enum Status { IDLE, LOADING, READY, EMPTY, FORBIDDEN, ERROR }
    private final Status status;
    private final Section section;
    private final AdminDashboardSnapshot dashboard;
    private final List<AdminListItem> items;
    private final String message;

    private AdminScreenState(Status status, Section section, AdminDashboardSnapshot dashboard,
            List<AdminListItem> items, String message) {
        this.status = status; this.section = section; this.dashboard = dashboard;
        this.items = Collections.unmodifiableList(new ArrayList<>(items == null
                ? Collections.emptyList() : items));
        this.message = message == null ? "" : message;
    }

    public static AdminScreenState dashboard(Status status, AdminDashboardSnapshot value, String message) {
        return new AdminScreenState(status, Section.ANALYTICS, value, null, message);
    }
    public static AdminScreenState list(Status status, Section section, List<AdminListItem> items, String message) {
        return new AdminScreenState(status, section, null, items, message);
    }
    public Status getStatus() { return status; }
    public Section getSection() { return section; }
    public AdminDashboardSnapshot getDashboard() { return dashboard; }
    public List<AdminListItem> getItems() { return items; }
    public String getMessage() { return message; }
}
