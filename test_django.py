#!/usr/bin/env python
import os
import sys

# Add the project directory to Python path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

try:
    print("Testing Django setup...")
    
    # Set Django settings
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "workshop_portal.settings")
    
    # Try to import Django
    import django
    print(f"Django version: {django.get_version()}")
    
    # Try to setup Django
    django.setup()
    print("Django setup successful!")
    
    # Test importing models
    from workshop_app.models import Profile, Workshop
    print("Models imported successfully!")
    
    # Test database connection
    from django.db import connection
    cursor = connection.cursor()
    print("Database connection successful!")
    
except Exception as e:
    print(f"Error: {e}")
    import traceback
    traceback.print_exc()
