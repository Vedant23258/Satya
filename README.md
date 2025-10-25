# SATYA Portal - Layout Verification System (Java Swing)

A comprehensive Java Swing application for managing property layouts, court cases, document verification, and administrative tasks. This is an advanced desktop application that replicates all the functionality of the original web-based SATYA Portal.

## 🌟 Features

### 🔐 Authentication & Security
- **Multi-role authentication** (Admin, User, Viewer)
- **Session management** with automatic timeout
- **Security violations tracking** (screenshot attempts, printing, copying)
- **Document protection** with watermarks and access controls
- **Role-based access control** for different functionalities

### 🔍 Layout Search & Management
- **Advanced search capabilities** with multiple filters
- **Real-time search results** with status-based color coding  
- **Export functionality** (CSV, Excel, PDF)
- **Detailed layout information** with coordinates and documentation
- **Status tracking** (Approved, Pending, Under Review, Rejected, Unauthorized)

### 🗺️ Interactive Map Visualization
- **OpenStreetMap integration** using JMapViewer
- **Color-coded markers** for different layout statuses
- **Click-to-view details** functionality
- **Search on map** with location centering
- **Coordinate display** and validation

### 📄 Secure Document Viewer
- **PDF document rendering** with security controls
- **Watermark overlay** for document protection
- **Violation detection** (print, copy, screenshot attempts)
- **Access logging** and audit trail
- **Full-screen secure viewing** mode

### ⚖️ Court Case Management
- **Case tracking** linked to layouts
- **Status management** (Active, Pending, Closed, Dismissed)
- **Hearing date tracking** and notifications
- **Document association** with cases
- **Case type categorization** (Civil, Criminal, Revenue)

### ⚙️ Administrative Panel (Admin Only)
- **Dashboard with statistics** and data visualization
- **User management** and role assignments
- **Layout approval workflow**
- **Violation monitoring** and reporting
- **AI document scanning** simulation
- **Feedback management** system
- **Data export and reporting** tools

### 🎨 Modern User Interface
- **Professional design** with consistent color scheme
- **Responsive layout** that adapts to screen sizes
- **Dark/Light theme support**
- **Intuitive navigation** with tabbed interface
- **Status indicators** and progress bars
- **Contextual menus** and keyboard shortcuts

### 🚀 Advanced Technical Features
- **Multi-language support** (English/Telugu)
- **Real-time clock** and session display
- **Auto-save functionality** for forms
- **Background task processing** with progress indicators
- **Memory-efficient data handling**
- **Comprehensive logging** system

## 🏗️ Project Structure

```
src/main/java/com/satya/portal/
├── SATYAPortalApp.java          # Main application entry point
├── LoginDialog.java             # Authentication dialog
├── MainFrame.java               # Primary application window
├── SearchPanel.java             # Layout search functionality
├── MapPanel.java                # Interactive map component
├── DocumentViewer.java          # Secure document viewing
├── AdminPanel.java              # Administrative tools
├── SecurityManager.java         # Security enforcement
├── AIScanner.java               # AI document processing
├── models/
│   ├── User.java                # User data model
│   ├── Layout.java              # Layout data model
│   ├── CourtCase.java           # Court case model
│   ├── Feedback.java            # User feedback model
│   └── Violation.java           # Security violation model
└── utils/
    ├── DataManager.java         # Data management singleton
    ├── ChartUtils.java          # Chart generation utilities
    ├── SecurityUtils.java       # Security helper functions
    └── MapUtils.java            # Map-related utilities
```

## 🛠️ Technology Stack

- **Java 11+** - Core programming language
- **Swing/AWT** - Desktop GUI framework
- **JMapViewer** - OpenStreetMap integration
- **FlatLaf** - Modern look and feel
- **JFreeChart** - Data visualization
- **Apache POI** - Excel file handling
- **iText** - PDF generation
- **Jackson** - JSON processing
- **H2 Database** - Local data storage
- **Maven** - Build and dependency management
- **Logback** - Logging framework

## 📋 Prerequisites

- **Java Development Kit (JDK) 11 or higher**
- **Maven 3.6+** for building the project
- **Minimum 4GB RAM** for optimal performance
- **1GB free disk space**
- **Internet connection** for map functionality

## 🚀 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-org/satya-portal-swing.git
cd satya-portal-swing
```

### 2. Build the Project
```bash
mvn clean compile
```

### 3. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.satya.portal.SATYAPortalApp"
```

### 4. Create Executable JAR
```bash
mvn clean package
java -jar target/satya-portal-swing-1.0.0.jar
```

### 5. Create Windows Executable (Optional)
```bash
mvn clean package
# This creates SATYA-Portal.exe in target directory
```

## 👥 Demo Accounts

The application includes pre-configured demo accounts for testing:

### Administrator Account
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** Admin
- **Access:** Full system access including admin panel

### User Account  
- **Username:** `user`
- **Password:** `user123`
- **Role:** User
- **Access:** Search, view, and document access

### Viewer Account
- **Username:** `viewer`
- **Password:** `viewer123`
- **Role:** Viewer
- **Access:** Read-only access to layouts and documents

## 📖 User Guide

### Getting Started
1. **Launch the application** and you'll see the login screen
2. **Select a role** from the dropdown (Admin/User/Viewer)
3. **Enter credentials** or use the demo accounts provided
4. **Click Login** to access the main application

### Navigation
- **Search Tab** - Find and filter property layouts
- **Map View Tab** - Visualize layouts on interactive map
- **Documents Tab** - Secure document viewing with protection
- **Court Cases Tab** - Manage legal cases related to layouts
- **Admin Tab** - Administrative functions (Admin users only)
- **Help Tab** - Documentation and user guidance

### Search Functionality
1. **Enter search terms** in the search field
2. **Apply filters** for status, owner, area range
3. **Use Advanced Filters** for detailed searching
4. **Double-click results** to view detailed information
5. **Export results** to CSV or Excel format

### Document Security
- Documents are **protected against copying, printing, and screenshots**
- **Violation attempts are logged** and reported to administrators
- **Watermarks are applied** to all sensitive documents
- **Access is tracked** with audit trails

### Administrative Functions
- **Manage user accounts** and roles
- **Monitor security violations** and access logs
- **Approve or reject** layout applications
- **Generate reports** and statistics
- **Configure system settings** and preferences

## 🔧 Configuration

### Application Properties
Create `src/main/resources/config.properties`:
```properties
# Application Configuration
app.name=SATYA Portal
app.version=1.0.0
app.theme=light

# Database Configuration
db.url=jdbc:h2:./data/satyaportal
db.username=sa
db.password=

# Map Configuration
map.default.latitude=14.4426
map.default.longitude=79.9865
map.default.zoom=10

# Security Configuration
security.max.violations=3
security.session.timeout=30
security.watermark.text=PROTECTED DOCUMENT - SATYA PORTAL
```

### Logging Configuration
Create `src/main/resources/logback.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/satya-portal.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## 🔒 Security Features

### Document Protection
- **Screenshot blocking** - Detects and prevents screenshot attempts
- **Print prevention** - Disables printing of sensitive documents  
- **Copy protection** - Prevents text selection and copying
- **Watermark overlay** - Adds visible protection to documents
- **Access logging** - Tracks who accessed what documents when

### User Management
- **Role-based access control** - Different permissions for different roles
- **Session management** - Automatic timeout and logout
- **Password validation** - Secure authentication process
- **Account lockout** - Protection against brute force attacks

### Audit Trail
- **User action logging** - Track all user activities
- **Security violation reporting** - Alert administrators to violations
- **Access monitoring** - Monitor document and data access
- **System event logging** - Comprehensive logging of system events

## 🐛 Troubleshooting

### Common Issues

#### Application Won't Start
- **Check Java version**: Ensure Java 11+ is installed
- **Verify classpath**: Make sure all dependencies are available
- **Check logs**: Look in `logs/satya-portal.log` for errors

#### Login Issues
- **Verify credentials**: Use the demo accounts provided
- **Check case sensitivity**: Usernames and passwords are case-sensitive
- **Clear session**: Restart the application to clear any cached sessions

#### Map Not Loading
- **Check internet connection**: Map requires internet access
- **Firewall settings**: Ensure the application can access external resources
- **Proxy configuration**: Configure proxy settings if needed

#### Performance Issues
- **Increase memory**: Use `-Xmx512m` JVM parameter
- **Close unused tabs**: Keep only necessary tabs open
- **Clear cache**: Restart application periodically

## 📝 Development

### Building from Source
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Create JAR file
mvn package

# Generate documentation
mvn javadoc:javadoc
```

### Adding New Features
1. **Create feature branch**: `git checkout -b feature/new-feature`
2. **Add implementation**: Follow the existing code structure
3. **Write tests**: Include unit tests for new functionality
4. **Update documentation**: Keep README and JavaDocs current
5. **Submit pull request**: Follow the contribution guidelines

### Code Style
- **Java coding standards**: Follow Oracle Java conventions
- **Consistent formatting**: Use 4-space indentation
- **Comprehensive comments**: Document complex logic
- **Error handling**: Proper exception handling throughout
- **Resource management**: Clean up resources properly

## 🤝 Contributing

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Email**: support@satya.gov.in
- **Documentation**: [User Manual](docs/USER_MANUAL.md)
- **FAQ**: [Frequently Asked Questions](docs/FAQ.md)
- **Issues**: [GitHub Issues](https://github.com/your-org/satya-portal-swing/issues)

## 🎯 Roadmap

### Version 1.1.0 (Planned)
- [ ] Database integration for persistent storage
- [ ] Advanced reporting with charts and graphs
- [ ] Email notifications for important events
- [ ] Bulk data import/export functionality
- [ ] Mobile companion app integration

### Version 1.2.0 (Future)
- [ ] Real-time collaboration features
- [ ] Integration with external GIS systems
- [ ] Advanced AI document processing
- [ ] Multi-language interface expansion
- [ ] Cloud deployment options

## 🏷️ Tags

`java` `swing` `desktop-application` `government` `gis` `document-management` `security` `layout-verification` `court-cases` `administrative-tools`

---

**© 2023 SATYA Portal Team. All rights reserved.**