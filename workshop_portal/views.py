# Django Imports
from django.shortcuts import redirect
from django.urls import reverse

def index(request):
    """Main portal view - redirects to workshop app"""
    return redirect("workshop_app:index")
